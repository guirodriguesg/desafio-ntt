package nttdata.bank.utils;

public class ConstatesUtils {

    public static final String REGEX_VALIDAR_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String BEARER_KEY = "bearer-key";
    public static final String HEADER_APIKEY_MOCKAPI = "x-api-key";
    public static final String HEADER_ACCESS_KEY = "access_key";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String URI_CONTA = "/api/v1/conta";
    public static final String URI_USUARIO = "/api/v1/usuario";
    public static final String URI_AUTENTICACAO = "/api/v1/autenticacao";
    public static final String[] URIS_SWAGGER = {"/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};

    public static final String MIME_XLS = ".xls";
    public static final String MIME_XLSX = ".xlsx";
    public static final String MIME_CSV = ".csv";
    public static final String MIME_PDF = ".pdf";

    public static final String DATE_FORMAT = "ddMMyyyy_HHmmss";
    public static final String DATE_FORMAT_BARRA = "dd/MM/yyyy HH:mm";

    public static final String R_SIFRAO = "R$";
    public static final String MOEDA_BRL = "BRL";

    public static final String ADMIN = "ADMINISTRADOR";


}
