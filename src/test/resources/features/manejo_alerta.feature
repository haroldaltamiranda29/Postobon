#language: es
#Autor: Yefri Ruiz Mosquera

@Regresion
Característica: Cargar diferentes tipos de archivos en plataformas distintas

	@CargarArchivos
  Esquema del escenario: Cargar diferentes tipos de archivos
    Dado el ingreso al sitio web <url>
    Cuando ejecuto accion sobre alert js 
    Ejemplos: 
      | url    |  
			|https://qa-personas.credinet.co/|
			
