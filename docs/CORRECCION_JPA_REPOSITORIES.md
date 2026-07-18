# Corrección v2.0.2: descubrimiento de repositorios y entidades JPA

## Síntoma

La aplicación fallaba al crear `EvaluacionCrediticiaRepositoryAdapter` porque
Spring no encontraba un bean de tipo
`EvaluacionCrediticiaSpringDataRepository`.

## Causa

La clase `MotorScoringApplication` está ubicada en:

```text
com.finanscore.motorscoring.bootstrap
```

`scanBasePackages` amplía el escaneo de componentes (`@Component`,
`@Repository`, `@Configuration`), pero el descubrimiento de repositorios
Spring Data y entidades JPA usa los paquetes de auto-configuración. Como los
repositorios y entidades están en paquetes hermanos dentro del módulo de
infraestructura, era necesario registrarlos explícitamente.

## Solución

Se añadió en el módulo de infraestructura:

```java
@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EvaluacionCrediticiaJpaEntity.class)
@EnableJpaRepositories(
    basePackageClasses = EvaluacionCrediticiaSpringDataRepository.class
)
public class JpaPersistenceConfiguration {
}
```

El uso de `basePackageClasses` evita cadenas de paquetes y toma como paquetes
base los de las clases marcadoras.

## Corrección preventiva adicional

El Dockerfile anterior copiaba un JAR con versión fija. El módulo bootstrap
ahora produce siempre:

```text
motor-scoring-app.jar
```

por medio de:

```xml
<finalName>motor-scoring-app</finalName>
```

Así, el Dockerfile no depende del número de versión del proyecto.
