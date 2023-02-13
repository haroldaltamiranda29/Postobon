#language: es
#Autor: Yefri Ruiz Mosquera
@RegresionAPIS
Característica: Consumir API´s


  Esquema del escenario: POST Token with JSON body

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    Entonces verifico el status code <StatusCode>
    Y guardo el token en archivo

    Ejemplos:
      | API  | Peticion | Enviroment | EndPoint | METODO | HEADERS                                                                                  | BODY                                            | StatusCode | PathFile | NameKeys |
    ##@externaldata@src/test/resources/datadriven/PruebasApis.xlsx@Post
|POST|Success|BASETOKEN|/Token|POST|Content-Type:application/json,Ocp-Apim-Subscription-Key:46ef8fb91fba4d48bf084ee606a9522c|{"Secret":"1aExe02C-U_mt0QrzP-ka43MSo6.Sq8_kw"}|200|||

  Esquema del escenario: GET with URL Params

    Dado quiero ejecutar el API <API> con la peticion <Peticion>
    Cuando consumo el api <API> con la data data de prueba
      | <Enviroment> | <EndPoint> | <METODO> | <HEADERS> | <BODY> | <PathFile> | <NameKeys> |
    #Entonces verifico el status code <StatusCode>
    Y las respuestas esperadas <RespuestasEsperadas> en las rutas <PATHS> del response

    Ejemplos:
      | API                                    | Peticion | Enviroment   | EndPoint                 | METODO     | HEADERS                                                                                                        | BODY                                    | StatusCode | PATHS                    | RespuestasEsperadas                                 | PathFile | NameKeys |
  ##@externaldata@src/test/resources/dataDriven/PruebasApis.xlsx@Hoja3
|Http_EntidadesMasivas_Consultar_Contrato| Success  |BASEPOSTOBONMASIVA| /Masivas/{origen}/{entidad} | GET-PARAMS | Content-Type:application/json,Ocp-Apim-Subscription-Key:46ef8fb91fba4d48bf084ee606a9522c,Authorization: Bearer |origen:PESV,entidad:Contrato|200|API Contrato|Data API Contrato Validada Exitosamente|||







