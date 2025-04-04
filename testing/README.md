# Тестирование Health Data Receiver

* Скрипт по проверки REST API контроллера сервиса: [get_version.sh](get_version.sh).
* Скрипт по проверке API мониторинга сервиса: [health.sh](health.sh).

## Тестовый HAPI FHIR сервер

* Скрипт для запуска/остановки: [fhir.sh](fhir.sh).
* Docker compose файл: [fhir-compose.yml](fhir-compose.yml).

### Ресурсы

| Ресурс                                                                   | URL                                    | Credentials |
|--------------------------------------------------------------------------|----------------------------------------|-------------|
| http://localhost:8091/                                                   | Overlay                                |             |
| http://localhost:8091/fhir/ <br/> http://localhost:8091/fhir/swagger-ui/ | Swagger-UI                             |             |
| FHIR DB                                                                  | jdbc:postgresql://localhost:35433/fhir | hapi / hapi |

## Тестовые HL7 данные

* Скрипт для отправки тестовых HL7 данных: [send_hl7.sh](send_hl7.sh).
* Директория, содержащая тестовые HL7 данные: [hl7data](hl7data).

### Генераторы данных

* [Random Name Generator](https://www.behindthename.com).
* [Fake Name Generator](https://www.fakenamegenerator.com/).

#### HL7 test message generator

* [HL7 test message generator (DPI-I-464) InterSystems Community opportunity idea](https://ideas.intersystems.com/ideas/DPI-I-464).

* IRIS-HL7v2Gen
  * [IRIS-HL7v2Gen: Dynamic HL7 Test Message and Structure Generation for Healthcare Integration](https://community.intersystems.com/post/iris-hl7v2gen-dynamic-hl7-test-message-and-structure-generation-healthcare-integration).
  * [iris-HL7v2Gen](https://openexchange.intersystems.com/package/iris-HL7v2Gen).
* [ks-fhir-gen](https://openexchange.intersystems.com/package/ks-fhir-gen).

### HAPI FHIR
* [HAPI FHIR Test/Demo Server R4 Endpoint](https://hapi.fhir.org/baseR4/swagger-ui/).
