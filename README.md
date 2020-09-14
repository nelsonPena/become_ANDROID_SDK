# Documentación de Become Android SDK
Este es un espacio para conocer a cerca de la app Become para validación biométrica de identidad en el sistema Android.

## Configuraciones de Gradle
### Instalación de Become

1. El archivo build.grade debe contar con una referencia al repositorio:
  
		 jcenter()
       
 2. Abra el archivo build.gradle dentro del directorio del módulo de su aplicación e incluya las siguientes dependencias en el archivo build.gradle de su aplicación:
 
		 implementation 'com.becomedigital:sdk-Android:LATEST_VERSION'
		 
	Ejemplo:
		 
		 implementation 'com.becomedigital:sdk-Android:2.1.1'
 
 3. Al realizar los pasos anteriores, debe sincronizar su proyecto con gradle.
 
 ## Inicialización de la SDK
En el método onCreate () de su clase de aplicación, inicialice Become utilizando el siguiente fragmento de código:

	public class MainActivity extends AppCompatActivity {
    //Con el fin de manejar las respuestas de inicio de sesión, debe crear un callback utilizando el siguiente fragmento de código
    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate (savedInstanceState);
	    setContentView (R.layout.activity_main);

    //Parámetros de configuración: El valor de los parámetros debe ser solicitado al contratar el servicio
    String validatiopnTypes =  "PASSPORT/LICENSE/DNI/VIDEO" ;  
    String clientSecret =  "FKLDM63GPH89TDSDFDSGFGISBWRQA25" ;  
    String clientId =  "abc_def" ;  
    String contractId =  "7";

    //Instancia para iniciar la interfaz
    BecomeResponseManager.getInstance ( ).startAutentication (MainActivity.this,  
        new BDIVConfig (clientId,  
                clientSecret,  
                contractId,  
                validatiopnTypes,  
                true,  
                null  
    ));
	  }
	}



 ## Posibles Errores

**1. Error con parámetros vacíos** 
Los siguientes parámetros son necesarios para la activación de la SDK por lo tanto su valor no debe ser vacío.
 
Parámetro | Valor
------------ | -------------
validatiopnTypes | ""
clientSecret | ""
clientId | ""
contractId  | ""


Mostrará el siguiente error por consola:

    parameters cannot be empty

**2. Video cómo único parámetro**
El atributo ***validationTypes*** no puede contener como único valor el parámetro ***VIDEO***

    validationTypes: "VIDEO"

Mostrará el siguiente error por consola:

    the process cannot be initialized with video only

## Validación del proceso
En este apartado encontrará la respuesta a partir de la validación del proceso realizado por la SDK y las estructuras internas que contienen los atributos encargados de capturar la información dada por el usuario:

**1. Estructura encargada de la definición del estado  de validación ***exitoso***:**

	    @Override
        public void onSuccess(final ResponseIV responseIV) {  
            String id = responseIV.getId ( );
            String created_at = responseIV.getCreated_at ( );
            String company = responseIV.getCompany ( );
            String fullname = responseIV.getFullname ( );
            String birth = responseIV.getBirth ( );
            String document_type = responseIV.getDocument_type ( );
            String document_number = responseIV.getDocument_number ( );
            Boolean face_match = responseIV.getFace_match ( );
            Boolean template = responseIV.getTemplate ( );
            Boolean alteration = responseIV.getAlteration ( );
            Boolean watch_list = responseIV.getWatch_list ( );
            String comply_advantage_result = responseIV.getComply_advantage_result ( );
            String comply_advantage_url = responseIV.getComply_advantage_url ( );
            String verification_status = responseIV.getVerification_status ( );
            String message = responseIV.getMessage ( );
            Integer responseStatus = responseIV.getResponseStatus ( );
            String textFinal = "id: "  +
                    "\ncreated_at: " + created_at +
                    "\ncompany: " + company +
                    "\nfullname: " + fullname +
                    "\nbirth: " + birth +
                    "\ndocument_type: " + document_type +
                    "\ndocument_number: " + document_number +
                    "\nface_match: " + face_match +
                    "\ntemplate: " + template +
                    "\nalteration: " + alteration +
                    "\nwatch_list: " + watch_list +
                    "\ncomply_advantage_result: " + comply_advantage_result +
                    "\ncomply_advantage_url: " + comply_advantage_url +
                    "\nverification_status: " + verification_status +
                    "\nmessage: " + message +
                    "\nresponseStatus: " + responseStatus;

            Log.d ("responseIV", textFinal);
        }
	
**2. Estructura encargada de la definición del estado  de validación ***cancelado*** por el usuario:**

	@Override  
	public void onCancel() { 
	    textResponse.setText ("Cancelado por el usuario ");  
	}
   
**3. Estructura encargada de la definición del estado  de validación ***error*** , este estado se presenta cuándo ocurren errores generales o de inicialización de parámetros :**

	 @Override  
	 public void onError(LoginError pLoginError) {
	    Log.d ("Error", pLoginError.getMessage ( ));
	 }

## Estructura para el retorno de la información
Los siguientes son los parámetros que permiten el retorno de la información capturada por el sistema.

	private String id;  
	private String created_at;  
	private String company;  
	private String fullname;  
	private String birth;  
	private String document_type;  
	private String document_number;  
	private Boolean face_match;  
	private Boolean template;  
	private Boolean alteration;  
	private Boolean watch_list;  
	private String comply_advantage_result;  
	private String comply_advantage_url;  
	private String verification_status;  
	private String message;  
	private Integer responseStatus;

Ejemplo de la respuesta:

	public void onSuccess(final ResponseIV responseIV) {  
	  String id = responseIV.getId ( );  
		String created_at = responseIV.getCreated_at ( );  
		String company = responseIV.getCompany ( );  
		String fullname = responseIV.getFullname ( );  
		String birth = responseIV.getBirth ( );  
		String document_type = responseIV.getDocument_type ( );  
		String document_number = responseIV.getDocument_number ( );  
		Boolean face_match = responseIV.getFace_match ( );  
		Boolean template = responseIV.getTemplate ( );  
		Boolean alteration = responseIV.getAlteration ( );  
		Boolean watch_list = responseIV.getWatch_list ( );  
		String comply_advantage_result = responseIV.getComply_advantage_result ( );  
		String comply_advantage_url = responseIV.getComply_advantage_url ( );  
		String verification_status = responseIV.getVerification_status ( );  
		String message = responseIV.getMessage ( );  
		Integer responseStatus = responseIV.getResponseStatus ( );  
		String textFinal = "id: " +  
             "\ncreated_at: " + created_at +  
             "\ncompany: " + company +  
             "\nfullname: " + fullname +  
             "\nbirth: " + birth +  
             "\ndocument_type: " + document_type +  
             "\ndocument_number: " + document_number +  
             "\nface_match: " + face_match +  
             "\ntemplate: " + template +  
             "\nalteration: " + alteration +  
             "\nwatch_list: " + watch_list +  
             "\ncomply_advantage_result: " + comply_advantage_result +  
             "\ncomply_advantage_url: " + comply_advantage_url +  
             "\nverification_status: " + verification_status +  
             "\nmessage: " + message +  
             "\nresponseStatus: " + responseStatus;  
  
	  textResponse.setText (textFinal);  
	  Log.d ("responseIV", textFinal);  
	}

## Implementación del proceso
Esta sección se encarga de proporcionar el fragmento de código para la implementación final del proceso.

	public class MainActivity extends AppCompatActivity {  
  	    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );  
	  
	  @SuppressLint("WrongThread")  
	  @Override  
	  protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate (savedInstanceState);  
	        setContentView (R.layout.activity_main);  
	  
	  Bitmap decodeResource = BitmapFactory.decodeResource (getResources ( ), R.drawable.become_icon);  
	  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ( );  
	  
	  decodeResource.compress (Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);  
	  byte[] byteArray = byteArrayOutputStream.toByteArray ( );  
	  
	  TextView textResponse = findViewById (R.id.textReponse);  
	  EditText textClientSecret = findViewById (R.id.cliensecretText);  
	  EditText textClientId = findViewById (R.id.clienidText);  
	  EditText textContractId = findViewById (R.id.ContractIdText);  
	  EditText textVlidationType = findViewById (R.id.validationType);  
	  Button btnAut = findViewById (R.id.btnAuth);  
	  btnAut.setOnClickListener (view -> {  
	  String validatiopnTypes = textVlidationType.getText ( ).toString ( ).isEmpty ( ) ? "VIDEO" : textVlidationType.getText ( ).toString ( );  
	  String clientSecret = textClientSecret.getText ( ).toString ( ).isEmpty ( ) ? "FKLDM63GPH89TISBXNZ4YJUE57WRQA25" : textClientSecret.getText ( ).toString ( );  
	  String clientId = textClientId.getText ( ).toString ( ).isEmpty ( ) ? "acc_demo" : textClientId.getText ( ).toString ( );  
	  String contractId = textContractId.getText ( ).toString ( ).isEmpty ( ) ? "2" : textContractId.getText ( ).toString ( );  
	  
	  BecomeResponseManager.getInstance ( ).startAutentication (MainActivity.this,  
	      new BDIVConfig (clientId,  
	      clientSecret,  
	      contractId,  
	      validatiopnTypes,  
	      true,  
	      byteArray  
	  ));  
    
	  BecomeResponseManager.getInstance ( ).registerCallback (mCallbackManager, new BecomeInterfaseCallback ( ) {  
	    @Override  	  
	    public void onSuccess(final ResponseIV responseIV) {  
	      String id = responseIV.getId ( );  
	      String created_at = responseIV.getCreated_at ( );  
	      String company = responseIV.getCompany ( );  
	      String fullname = responseIV.getFullname ( );  
	      String birth = responseIV.getBirth ( );  
	      String document_type = responseIV.getDocument_type ( );  
	      String document_number = responseIV.getDocument_number ( );  
	      Boolean face_match = responseIV.getFace_match ( );  
	      Boolean template = responseIV.getTemplate ( );  
	      Boolean alteration = responseIV.getAlteration ( );  
	      Boolean watch_list = responseIV.getWatch_list ( );  
	      String comply_advantage_result = responseIV.getComply_advantage_result ( );  
	      String comply_advantage_url = responseIV.getComply_advantage_url ( );  
	      String verification_status = responseIV.getVerification_status ( );  
	      String message = responseIV.getMessage ( );  
	      Integer responseStatus = responseIV.getResponseStatus ( );  
	      String textFinal = "id: " +  
	                             "\ncreated_at: " + created_at +  
	                             "\ncompany: " + company +  
	                             "\nfullname: " + fullname +  
	                             "\nbirth: " + birth +  
	                             "\ndocument_type: " + document_type +  
	                             "\ndocument_number: " + document_number +  
	                             "\nface_match: " + face_match +  
	                             "\ntemplate: " + template +  
	                             "\nalteration: " + alteration +  
	                             "\nwatch_list: " + watch_list +  
	                             "\ncomply_advantage_result: " + comply_advantage_result +  
	                             "\ncomply_advantage_url: " + comply_advantage_url +  
	                             "\nverification_status: " + verification_status +  
	                             "\nmessage: " + message +  
	                             "\nresponseStatus: " + responseStatus;  
	  
	  textResponse.setText (textFinal);  
	  Log.d ("responseIV", textFinal);  
	  }  
	  
	  @Override  
	  public void onCancel() {  
	      textResponse.setText ("Cancelado por el usuario ");	  
	  }  
	  
	  @Override  
	  public void onError(LoginError pLoginError) {  
	      textResponse.setText (pLoginError.getMessage ( ));  
	      Log.d ("Error", pLoginError.getMessage ( ));  
	  }  
	  
	       });  
	     });  	  
 	   }  
	}

## Requerimientos

* **Tecnologias**
	
	Android
	
## Vídeo de integración del SDK Become para Android
