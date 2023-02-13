#language: es
#Autor: Yefri Ruiz Mosquera

@RegresionAPIS
Característica: Consumir API´s

  Esquema del escenario: GET with URL Params

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API | Peticion | Enviroment | EndPoint      | METODO | HEADERS                       | BODY | StatusCode | PATHS    | RespuestasEsperadas | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/Prueba.xlsx@Get
|GET|Success|BASE|/get?lol=true|GET|Content-Type:application/json||200|args.lol|true|||

  @Post
  Esquema del escenario: POST with JSON body

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API  | Peticion | Enviroment | EndPoint | METODO | HEADERS                       | BODY                   | StatusCode | PATHS          | RespuestasEsperadas | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/Prueba.xlsx@Post
|POST|Success|BASE|/post|POST|Content-Type:application/json|{ "something": "cool"}|200|json.something|cool|||

  @PostToken
  Esquema del escenario: POST Token with JSON body

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API  | Peticion | Enviroment | EndPoint | METODO | HEADERS                       | BODY                   | StatusCode | PATHS          | RespuestasEsperadas | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/PruebasApis.xlsx@Post
|POST|Success|BASETOKEN|/Token|POST|Content-Type:application/json,Ocp-Apim-Subscription-Key:46ef8fb91fba4d48bf084ee606a9522c|{"Secret":"1aExe02C-U_mt0QrzP-ka43MSo6.Sq8_kw"}|200|||


  Esquema del escenario: DELETE request

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API    | Peticion | Enviroment | EndPoint | METODO | HEADERS                       | BODY | StatusCode | PATHS | RespuestasEsperadas       | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/Prueba.xlsx@Delete
|DELETE|Success|BASE|/delete|DELETE|Content-Type:application/json||200|url|http://httpbin.org/delete|||

  Esquema del escenario: PUT with form data

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API | Peticion | Enviroment | EndPoint | METODO | HEADERS                       | BODY                  | StatusCode | PATHS         | RespuestasEsperadas | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/Prueba.xlsx@Put
|PUT|Success|BASE|/put|PUT|Content-Type:application/json|{ "quotient": "224" }|200|json.quotient|224|||
