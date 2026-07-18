motor-scoring-crediticio/
│
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── finanscore/
│   │   │           └── motorscoring/
│   │   │
│   │   │               ├── MotorScoringApplication.java
│   │   │
│   │   │               ├── domain/
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Solicitante.java
│   │   │               │   │   ├── SolicitudCredito.java
│   │   │               │   │   ├── ProductoCrediticio.java
│   │   │               │   │   ├── ModeloScoring.java
│   │   │               │   │   ├── VersionModelo.java
│   │   │               │   │   ├── FactorScoring.java
│   │   │               │   │   ├── ReglaEvaluacion.java
│   │   │               │   │   ├── EvaluacionCrediticia.java
│   │   │               │   │   └── ResultadoFactor.java
│   │   │               │   │
│   │   │               │   ├── valueobject/
│   │   │               │   │   ├── Dinero.java
│   │   │               │   │   ├── Porcentaje.java
│   │   │               │   │   ├── PuntajeCrediticio.java
│   │   │               │   │   ├── RelacionDeudaIngreso.java
│   │   │               │   │   ├── CapacidadPago.java
│   │   │               │   │   └── IdentificadorExterno.java
│   │   │               │   │
│   │   │               │   ├── enums/
│   │   │               │   │   ├── EstadoSolicitud.java
│   │   │               │   │   ├── EstadoProducto.java
│   │   │               │   │   ├── EstadoModelo.java
│   │   │               │   │   ├── EstadoVersionModelo.java
│   │   │               │   │   ├── EstadoEvaluacion.java
│   │   │               │   │   └── ResultadoScoring.java
│   │   │               │   │
│   │   │               │   ├── service/
│   │   │               │   │   ├── CalculadorScoring.java
│   │   │               │   │   ├── CalculadorCapacidadPago.java
│   │   │               │   │   ├── CalculadorRelacionDeudaIngreso.java
│   │   │               │   │   └── EvaluadorReglasExcluyentes.java
│   │   │               │   │
│   │   │               │   ├── repository/
│   │   │               │   │   ├── SolicitanteRepository.java
│   │   │               │   │   ├── SolicitudCreditoRepository.java
│   │   │               │   │   ├── ProductoCrediticioRepository.java
│   │   │               │   │   ├── ModeloScoringRepository.java
│   │   │               │   │   └── EvaluacionCrediticiaRepository.java
│   │   │               │   │
│   │   │               │   └── exception/
│   │   │               │       ├── DomainException.java
│   │   │               │       ├── SolicitudNoEvaluableException.java
│   │   │               │       ├── ModeloActivoNoEncontradoException.java
│   │   │               │       └── PuntajeInvalidoException.java
│   │   │
│   │   │               ├── application/
│   │   │               │   ├── usecase/
│   │   │               │   │   ├── CrearSolicitudCreditoUseCase.java
│   │   │               │   │   └── EjecutarEvaluacionScoringUseCase.java
│   │   │               │   │
│   │   │               │   ├── command/
│   │   │               │   │   ├── CrearSolicitudCreditoCommand.java
│   │   │               │   │   └── EjecutarEvaluacionScoringCommand.java
│   │   │               │   │
│   │   │               │   ├── dto/
│   │   │               │   │   ├── SolicitudCreditoDto.java
│   │   │               │   │   ├── EvaluacionScoringDto.java
│   │   │               │   │   └── ResultadoFactorDto.java
│   │   │               │   │
│   │   │               │   ├── validator/
│   │   │               │   │   ├── SolicitudCreditoValidator.java
│   │   │               │   │   └── SolicitudEvaluacionValidator.java
│   │   │               │   │
│   │   │               │   ├── mapper/
│   │   │               │   │   ├── SolicitudCreditoMapper.java
│   │   │               │   │   └── EvaluacionScoringMapper.java
│   │   │               │   │
│   │   │               │   └── service/
│   │   │               │       └── EvaluacionScoringApplicationService.java
│   │   │
│   │   │               ├── infrastructure/
│   │   │               │   ├── persistence/
│   │   │               │   │   ├── entity/
│   │   │               │   │   │   ├── SolicitanteJpaEntity.java
│   │   │               │   │   │   ├── SolicitudCreditoJpaEntity.java
│   │   │               │   │   │   ├── ProductoCrediticioJpaEntity.java
│   │   │               │   │   │   ├── ModeloScoringJpaEntity.java
│   │   │               │   │   │   ├── VersionModeloJpaEntity.java
│   │   │               │   │   │   ├── FactorScoringJpaEntity.java
│   │   │               │   │   │   ├── ReglaEvaluacionJpaEntity.java
│   │   │               │   │   │   ├── EvaluacionCrediticiaJpaEntity.java
│   │   │               │   │   │   └── ResultadoFactorJpaEntity.java
│   │   │               │   │   │
│   │   │               │   │   ├── springdata/
│   │   │               │   │   │   ├── SpringDataSolicitudRepository.java
│   │   │               │   │   │   ├── SpringDataProductoRepository.java
│   │   │               │   │   │   ├── SpringDataModeloRepository.java
│   │   │               │   │   │   └── SpringDataEvaluacionRepository.java
│   │   │               │   │   │
│   │   │               │   │   └── adapter/
│   │   │               │   │       ├── SolicitudCreditoRepositoryAdapter.java
│   │   │               │   │       ├── ProductoCrediticioRepositoryAdapter.java
│   │   │               │   │       ├── ModeloScoringRepositoryAdapter.java
│   │   │               │   │       └── EvaluacionCrediticiaRepositoryAdapter.java
│   │   │               │   │
│   │   │               │   ├── config/
│   │   │               │   │   ├── BeanConfiguration.java
│   │   │               │   │   ├── JpaConfiguration.java
│   │   │               │   │   └── OpenApiConfiguration.java
│   │   │               │   │
│   │   │               │   └── exception/
│   │   │               │       └── PersistenceException.java
│   │   │
│   │   │               └── presentation/
│   │   │                   ├── controller/
│   │   │                   │   └── SolicitudCreditoController.java
│   │   │                   │
│   │   │                   ├── request/
│   │   │                   │   ├── CrearSolicitudCreditoRequest.java
│   │   │                   │   └── EjecutarEvaluacionRequest.java
│   │   │                   │
│   │   │                   ├── response/
│   │   │                   │   ├── SolicitudCreditoResponse.java
│   │   │                   │   ├── EvaluacionScoringResponse.java
│   │   │                   │   └── ErrorResponse.java
│   │   │                   │
│   │   │                   └── exception/
│   │   │                       └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__crear_tablas.sql
│   │               └── V2__insertar_datos_iniciales.sql
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── finanscore/
│                   └── motorscoring/
│                       ├── domain/
│                       │   ├── CalculadorScoringTest.java
│                       │   ├── CalculadorCapacidadPagoTest.java
│                       │   └── SolicitudCreditoTest.java
│                       │
│                       ├── application/
│                       │   ├── CrearSolicitudCreditoUseCaseTest.java
│                       │   └── EjecutarEvaluacionScoringUseCaseTest.java
│                       │
│                       └── integration/
│                           ├── SolicitudCreditoRepositoryIntegrationTest.java
│                           └── SolicitudCreditoControllerIntegrationTest.java