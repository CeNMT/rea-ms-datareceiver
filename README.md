# Health Data Receiver

Сервис приема медицинских данных и преобразования их в ресурсы `FHIR`. Для конвертации `HL7` данных используется код на основе библиотек [LinuxForHealth HL7 to FHIR Converter](https://github.com/LinuxForHealth/hl7v2-fhir-converter), `HAPI` для [HL7](https://hapifhir.github.io/hapi-hl7v2/) и [FHIR](https://hapifhir.io/).

Поддерживаемые стандарты на данный момент:
* [HL7 2.x](https://www.hl7.org/implement/standards/index.cfm).
