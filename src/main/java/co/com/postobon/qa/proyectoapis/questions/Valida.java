package co.com.postobon.qa.proyectoapis.questions;

import co.com.postobon.qa.proyectoapis.utils.ValidacionesDB;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Valida implements Question<List<String>> {

    private List<String> listaPath;

    public Valida(List<String> listaPath) {
        this.listaPath = listaPath;
    }


    public static Valida bodyRespuesta(List<String> listaPath) {
        return new Valida(listaPath);
    }


    @Override
    public List<String> answeredBy(Actor actor) {

        String respuestaValidacion = "";
        Response lastResponse = SerenityRest.lastResponse();
        List<String> resultado = new ArrayList<>();
        try {
            if (listaPath.contains("API Departamento")) {
                respuestaValidacion = ValidacionesDB.ValidacionDepartamento(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API DepartamentoID")) {
                respuestaValidacion = ValidacionesDB.ValidacionDepartamento(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MacroCanal")) {
                respuestaValidacion = ValidacionesDB.ValidacionMacroCanal(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MacroCanalID")) {
                respuestaValidacion = ValidacionesDB.ValidacionMacroCanal(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MacroSegmento")) {
                respuestaValidacion = ValidacionesDB.ValidacionMacroSegmento(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MacroSegmentoID")) {
                respuestaValidacion = ValidacionesDB.ValidacionMacroSegmento(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API SubTipologia")) {
                respuestaValidacion = ValidacionesDB.ValidacionGrupoSubtipologia(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API SubTipologiaID")) {
                respuestaValidacion = ValidacionesDB.ValidacionGrupoSubtipologia(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Barrio")) {
                respuestaValidacion = ValidacionesDB.ValidacionBarrio(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Operaciones")) {
                respuestaValidacion = ValidacionesDB.ValidacionOperaciones(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API OperacionesID")) {
                respuestaValidacion = ValidacionesDB.ValidacionOperaciones(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MotivoPedidos")) {
                respuestaValidacion = ValidacionesDB.ValidacionMotivoPedidos(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MotivoPedidosID")) {
                respuestaValidacion = ValidacionesDB.ValidacionMotivoPedidos(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Municipios")) {
                respuestaValidacion = ValidacionesDB.ValidacionMunicipio(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MunicipioID")) {
                respuestaValidacion = ValidacionesDB.ValidacionMunicipio(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Ventas")) {
                respuestaValidacion = ValidacionesDB.ValidacionZonaVenta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API VentasID")) {
                respuestaValidacion = ValidacionesDB.ValidacionZonaVenta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API PartidasAbiertas")) {
                respuestaValidacion = ValidacionesDB.ValidacionPartidaAbierta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API PartidasAbiertasID")) {
                respuestaValidacion = ValidacionesDB.ValidacionPartidaAbierta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API MisionCompra")) {
                respuestaValidacion = ValidacionesDB.ValidacionMisionCompra(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Sector")) {
                respuestaValidacion = ValidacionesDB.ValidacionSector(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API ModeloAtencion")) {
                respuestaValidacion = ValidacionesDB.ValidacionModeloAtencion(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API ModeloAtencionID")) {
                respuestaValidacion = ValidacionesDB.ValidacionModeloAtencion(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API Pais")) {
                respuestaValidacion = ValidacionesDB.ValidacionPais(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API PaisID")) {
                respuestaValidacion = ValidacionesDB.ValidacionPais(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API CondicionPago")) {
                respuestaValidacion = ValidacionesDB.ValidacionCodicionPago(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API CondicionPagoID")) {
                respuestaValidacion = ValidacionesDB.ValidacionCodicionPago(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API OrganizacionesVenta")) {
                respuestaValidacion = ValidacionesDB.ValidacionOrganizacionVenta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API OrganizacionesVenta")) {
                respuestaValidacion = ValidacionesDB.ValidacionOrganizacionVenta(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API UnidadMedida")) {
                respuestaValidacion = ValidacionesDB.ValidacionUnidadMedida(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API CondicionPago")) {
                respuestaValidacion = ValidacionesDB.ValidacionUnidadMedida(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            } else if (listaPath.contains("API EsquemaCalculo")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaEsquemaCalculo(lastResponse, listaPath.get(0).toString());
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API DireccionCardinalidad")) {
                respuestaValidacion = ValidacionesDB.ValidacionDireccionesCardinalidad(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API DireccionComplemento")) {
                respuestaValidacion = ValidacionesDB.ValidacionDireccionesComplemento(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API DireccionNomenclatura")) {
                respuestaValidacion = ValidacionesDB.ValidacionDireccionesNomenclatura(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API DireccionVia")) {
                respuestaValidacion = ValidacionesDB.ValidacionDireccionesVia(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API RepresentanteVenta")) {
                respuestaValidacion = ValidacionesDB.ValidacionRepresentanteVenta(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API Sociedad")) {
                respuestaValidacion = ValidacionesDB.ValidacionSociedades(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API CentroSociedad")) {
                respuestaValidacion = ValidacionesDB.ValidacionCentroSociedad(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API ClaseCondicion")) {
                respuestaValidacion = ValidacionesDB.ValidacionclaseCondicion(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API SecuenciaAcceso")) {
                respuestaValidacion = ValidacionesDB.ValidacionSecuenciaAcceso(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API SecuenciaAccesoCampo")) {
                respuestaValidacion = ValidacionesDB.ValidacionSecuenciaAccesoCampo(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API GrupoArticulo")) {
                respuestaValidacion = ValidacionesDB.ValidacionGrupoArticulos(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API ClaseCondicionID")) {
                respuestaValidacion = ValidacionesDB.ValidacionclaseCondicion(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API RutasCentroId")) {
                respuestaValidacion = ValidacionesDB.ValidacionRutasCentroId(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API Rutas")) {
                respuestaValidacion = ValidacionesDB.ValidacionRutas(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API JerarquiaMaterial")) {
                respuestaValidacion = ValidacionesDB.ValidacionJerarquiaMateriales(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API GrupoVendedores")) {
                respuestaValidacion = ValidacionesDB.ValidacionGrupoVendedores(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API JerarquiaRepresentante")) {
                respuestaValidacion = ValidacionesDB.ValidacionJerarquiaSupervisor(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API Calendario")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaCalendario(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API Contrato")) {
                    respuestaValidacion = ValidacionesDB.ValidacionMasivaContrato(lastResponse, listaPath.get(0));
                    resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API PresupuestoEnvase")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaPresupuestoEnvase(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API ListadosExclusion")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaListadosExclusion(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API EquipoVehiculo")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaEquipoVehiculo(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API EquipoNevera")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaEquipoNevera(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            }else if (listaPath.contains("API MovimientoEnvase")) {
                respuestaValidacion = ValidacionesDB.ValidacionMasivaMovimientoEnvase(lastResponse, listaPath.get(0));
                resultado.add(respuestaValidacion);
            } else {
                for (String s : listaPath) {
                    Object path = lastResponse.path(s);
                    if (path == null) {
                        resultado.add("no tiene mensaje");
                    } else {
                        resultado.add(path.toString());
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultado;
    }
}
