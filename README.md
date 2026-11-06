# Envios Service — Diagnóstico y Mejora Guiada por Métricas

## Entorno de análisis
Se seleccionó SonarQube Community Edition ejecutado localmente mediante un contenedor Docker. Esta decisión se fundamenta en:
- **Independencia y soberanía del entorno:** Autonomía operativa sin depender de conectividad a internet ni de restricciones de planes en la nube.
- **Control total de configuración y privacidad:** Capacidad para configurar Quality Profiles, Quality Gates y reglas estáticas localmente sin exponer el código fuente ni reportes en servidores externos.

## Decisiones de diseño

### 1. Entorno de análisis
Justificada en la sección superior bajo los criterios de independencia operativa y gobierno local de reglas de inspección estática.

### 2. Exclusiones del análisis
En esta fase se decidió no configurar exclusiones en `sonar.exclusions`. El proyecto consta exclusivamente de lógica de negocio pura y sus respectivas pruebas unitarias. Al no existir clases de configuración, DTOs sin lógica ni métodos de arranque (`Main`), cualquier exclusión ocultaría código de dominio relevante y distorsionaría artificialmente el cálculo de cobertura real.

### 3. Métrica usada como señal principal
Se seleccionó la **Complejidad Ciclomática (Cyclomatic Complexity)** como señal principal para identificar la deuda técnica a intervenir. Mientras que la cobertura (18% en ramas) indica qué partes del código no han sido ejecutadas y la complejidad cognitiva mide el esfuerzo de comprensión para un humano, la complejidad ciclomática mide matemáticamente el número de caminos linealmente independientes a través del grafo de flujo de control ($CC = 20$). Esta métrica es la causante raíz de la baja cobertura: un método con $CC = 20$ exige como mínimo 20 casos de prueba independientes para cubrir todas sus ramas. Por tanto, la refactorización atacó la raíz estructural (reducir los puntos de decisión) y no solo añadir pruebas sobre un método monolítico.

### 4. Selección del patrón de diseño para la refactorización
Se evaluaron tres patrones de diseño para mitigar la alta complejidad ciclomática causada por las cadenas de condicionales:
1. **Descarte de State:** El servicio de cálculo no mantiene estados internos entre invocaciones sucesivas ni define transiciones de ciclo de vida que alteren su comportamiento futuro. Cada cálculo es idempotente y sin estado (*stateless*).
2. **Descarte de Chain of Responsibility:** No existe una secuencia encadenada de múltiples manejadores que deban evaluar la petición progresivamente o donde el orden de precedencia sea determinante. Un envío no es procesado por múltiples etapas secuenciales de transporte.
3. **Selección de Strategy:** El problema consiste puntualmente en la selección de un algoritmo concreto de tarificación a partir de un valor discreto e invariable en la petición (`metodoEnvio`). Cada estrategia (`EnvioEstandarStrategy`, `EnvioExpressStrategy`, `EnvioOvernightStrategy`, `EnvioInternacionalStrategy`) encapsula exclusivamente su fórmula de cálculo por peso y recargos por distancia, reduciendo drásticamente la complejidad ciclomática del método original de 20 a 5.

**Justificación de responsabilidades:**
- **En las Estrategias:** Se encapsuló la lógica variante (fórmulas de tramos de peso, distancias máximas y recargos propios de cada tipo de transporte).
- **En el Orquestador (`ServicioCalculoEnvio`):** Se mantuvieron deliberadamente las reglas de negocio transversales (recargo por paquete frágil y descuentos según el tipo de cliente). Trasladar estas reglas a cada estrategia habría generado duplicación innecesaria de código (violando el principio DRY).

### 5. Criterio de pruebas agregadas para incremento de cobertura
El diseño de la nueva suite de pruebas no buscó inflar el porcentaje de cobertura con ejecuciones triviales (como llamadas sin assertions), sino ejercitar de manera exhaustiva todas las ramas que el reporte inicial de JaCoCo dejó al descubierto (31 de 38 ramas):
1. **Casos de equivalencia y frontera por tramos de peso:** Pruebas parametrizadas (`@ParameterizedTest` con `@CsvSource`) en cada estrategia para los límites: $\le 5$ kg, $5 < \text{peso} \le 20$ kg, y $> 20$ kg.
2. **Condiciones de distancia extrema:** Se agregaron pruebas para recargos en distancias superiores a 500 km (Estándar y Express), 5000 km (Internacional) y la excepción de bloqueo de distancia $> 300$ km en Overnight.
3. **Reglas transversales en el servicio:** Se cubrió el recargo del 15% por fragilidad, el descuento del 15% a clientes corporativos y el rechazo con `IllegalArgumentException` ante métodos no soportados.

### 6. Realismo de los umbrales del Quality Gate
La configuración de los umbrales para el Quality Gate y el Quality Profile "Estándar Laboratorio" se fundamenta en los estándares de mantenibilidad y cobertura de la industria (Tabla 3.2 y Sección 2.3 de la guía de la unidad):
1. **Límite de Complejidad Ciclomática ($CC \le 10$) por método (Regla S1541):** Se estableció un umbral de 10 puntos de decisión por método por ser el límite superior del rango clasificado como "Moderada". Métodos con $CC \le 10$ son comprensibles, testeables sin proliferación desmedida de casos de prueba y estables ante mantenimiento. Exigir un umbral inferior (como $CC \le 5$) forzaría una sobre-fragmentación artificial del código en métodos de una sola línea, mientras que tolerar $CC > 10$ abriría la puerta a métodos monolíticos frágiles.
2. **Umbral de Cobertura ($Coverage \ge 70\%$):** Exigir un 100% de cobertura es una práctica contraproducente que genera incentivos perversos (desarrolladores redactando tests triviales sin aserciones útiles solo para cumplir una cuota numérica). Un umbral del 70% garantiza que la totalidad de la lógica de negocio nuclear y sus caminos críticos estén exhaustivamente verificados, otorgando a su vez flexibilidad ante código boilerplate o de infraestructura sin comprometer la confiabilidad.
3. **Líneas duplicadas ($\le 5\%$):** Previene la propagación de deuda técnica por "copiar y pegar" bloques algorítmicos entre clases de transporte.

## Comparación antes / después

| Métrica | Antes (Parte 1) | Después (Parte 2) |
|---|---|---|
| Complejidad ciclomática de `calcularCosto()` | 20 | 5 |
| Cobertura de ramas de la lógica de envío | 18% | 90% |
| Cobertura de líneas global | 24% | 94% |
| Code Smells nuevos | 0 | 0 |
| Quality Gate | — | Failed (Condición Duplicated Lines 7.95% > 5%) |

> **Nota sobre el resultado del Quality Gate:** El Quality Gate reportó *Failed* debido a que la métrica de líneas duplicadas alcanzó un 7.95% frente al umbral estricto del 5%. Esta duplicación es estructuralmente esperada e inherente a la naturaleza del dominio, dado que las cuatro estrategias de transporte evalúan internamente los mismos tramos de escala de peso con estructuras algorítmicas idénticas pero con coeficientes tarifarios específicos.

## Capturas

### Análisis inicial (Parte 1)
![dashboard inicial sonar](docs/sonar-dashboard-inicial.png)

![jacoco inicial](docs/jacoco-inicial.png)

### Análisis final tras refactorización (Parte 2)
![sonar dashboard final](docs/sonar-dashboard-final.png)

![jacoco final](docs/jacoco-final.png)

## Herramientas utilizadas
- Java 21, Apache Maven
- SonarQube Community Edition (Docker), JaCoCo
- Git, GitHub

## Conclusiones
La inspección estática guiada por métricas reales permitió diagnosticar objetivamente que la baja cobertura inicial era consecuencia directa de una excesiva complejidad ciclomática concentrada en un método monolítico. La aplicación del patrón Strategy desacopló las fórmulas de transporte, reduciendo la complejidad en un 75% y facilitando la creación de pruebas unitarias que elevaron la cobertura de ramas del 18% al 90%. Asimismo, la configuración de Quality Gates demostró cómo los umbrales automatizados protegen la mantenibilidad del código en ciclos de entrega continua.