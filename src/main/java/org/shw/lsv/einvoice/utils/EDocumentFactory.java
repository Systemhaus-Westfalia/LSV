/**
 * 
 */
package org.shw.lsv.einvoice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import org.adempiere.core.domains.models.X_E_Activity;
import org.adempiere.core.domains.models.X_E_DocType;
import org.apache.commons.lang3.StringUtils;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPOS;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.json.JSONArray;
import org.json.JSONObject;
import org.shw.lsv.einvoice.feccfcreditofiscalv3.CreditoFiscal;

/**
 * 
 */
public abstract class EDocumentFactory {
	public abstract void generateJSONInputData();
	public abstract StringBuffer getEDocumentErrorMessages(); 
	public abstract EDocument generateEDocument();
	public abstract String createJsonString() throws Exception;

	protected MClient	client = null;
	protected MOrgInfo orgInfo = null;
	protected String trxName;
	protected Properties contextProperties;
	protected JSONObject jsonInputToFactory;  // Will contain data passed to factory
	protected Language languageUsed = null;
	
	public static String sqlCuerpoDocumento 		= "SELECT * FROM	EI_Invoiceline_Cumm "
														+ " WHERE c_invoice_id = ? ";
	public static String sqlApendice 				= "SELECT (invoiceinfo) as invoiceinfo FROM shw_c_invoice_header_vt i"
														+ " WHERE AD_LANGUAGE = 'es_SV' AND C_Invoice_ID=?";
	
	public static int	lineNo 										= 50;
	public static String CUERPODOCUMENTO_VENTANOSUJETO				= "ventanosuj";
	public static String CUERPODOCUMENTO_VENTAEXENTA				= "ventaex";
	public static String CUERPODOCUMENTO_VENTAGRAVADA				= "ventagravada";
	public static String CUERPODOCUMENTO_VENTANOGRAVADA				= "cuentaajena";
	public static String CUERPODOCUMENTO_PRODUCTVALUE				= "productvalue";
	public static String CUERPODOCUMENTO_PRODUCTNAME				= "name";
	public static String CUERPODOCUMENTO_PRICEACTUAL				= "priceactual";
	public static String CUERPODOCUMENTO_QTYINVOICED				= "qtyinvoiced";
	public static String CUERPODOCUMENTO_LINETOTALAMT   			= "linetotalamt";
	

	public static String TAXINDICATOR_IVA 							= "IVA";
	public static String TAXINDICATOR_NSUJ 							= "NSUJ";
	public static String TAXINDICATOR_EXT 							= "EXT";
	public static String TAXINDICATOR_RET 							= "RET";
	public static String CHARGETYPE_CTAJ	 						= "CTAJ";
	
	public static String Columnname_E_Activity_ID 					= "E_Activity_ID";
	
	
	

	
	
	public EDocumentFactory(String trxName, Properties contextProperties, MClient client, MOrgInfo orgInfo) {
		this.trxName = trxName;
		this.contextProperties = contextProperties;
		this.client = client;
		this.orgInfo = orgInfo;
		String lang = client.getAD_Language();	
		this.languageUsed = Language.getLanguage(lang);
			
			
	}
	
	public String getCodigoGeneracion (String eDocumentAsJsonString) {

        JSONObject eDocumentAsJson  = new JSONObject(eDocumentAsJsonString);
        JSONObject identification = (JSONObject)eDocumentAsJson.get(EDocument.IDENTIFICACION);
        String codigoGeneracion = identification.getString(EDocument.CODIGOGENERACION);
		return codigoGeneracion;
	}
	
	public String getNumeroControl (String eDocumentAsJsonString) {

        JSONObject eDocumentAsJson  = new JSONObject(eDocumentAsJsonString);
        JSONObject identification = (JSONObject)eDocumentAsJson.get(EDocument.IDENTIFICACION);
        String codigoGeneracion = identification.getString(EDocument.NUMEROCONTROL);
		return codigoGeneracion;
	}
	
	public boolean writeToFile (String json, MInvoice invoice, String directory) {
		System.out.println("Anulacion: start writing to file");
		try
		{
			Path rootpath = Paths.get(directory);
			if (!Files.exists(rootpath)) {
				return false;
			}    	

			directory = (directory.endsWith("/")
					|| directory.endsWith("\\"))
					? directory:directory + "/";
			Path path = Paths.get(directory + invoice.getDateAcct().toString().substring(0, 10) + "/");
			Files.createDirectories(path);
			//java.nio.file.Files;
			Files.createDirectories(path);
			String filename = path +"/" + invoice.getDocumentNo().replace(" ", "") + ".json"; 
			File out = new File (filename);
			Writer fw = new OutputStreamWriter(new FileOutputStream(out, false), "UTF-8");
			fw.write(json);
			fw.flush ();
			fw.close ();
			float size = out.length();
			size /= 1024;
			System.out.println("File size: " + out.getAbsolutePath() + " - " + size + " kB");
			System.out.println("Printed To: " + filename);
			System.out.println("Anulacion: end writing to file");
			return true;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	

	public JSONObject generateApendiceInputData(int invoiceID) {
//		String sqlSelect = "SELECT (invoiceinfo) as invoiceinfo FROM shw_c_invoice_header_vt i"
//				+ " WHERE AD_LANGUAGE = 'es_SV' AND C_Invoice_ID=?";
		String infoInvoice = DB.getSQLValueStringEx(null, sqlApendice, invoiceID);
		if (infoInvoice == null || infoInvoice.length()==0)
			infoInvoice = " ";
		ArrayList<String> info = new ArrayList<String>();
		int part = 150;
		if (infoInvoice.length()<= 150) {
			info.add(infoInvoice);
		}
		while(infoInvoice.length() > 150) {
			part = infoInvoice.length()>=150?150:infoInvoice.length();
			String infopart = infoInvoice.substring(0,part);
			info.add(infopart);
			infoInvoice = infoInvoice.substring(part, infoInvoice.length());
			if (info.size() == 10)
				break;
		}
		JSONObject jsonApendice = new JSONObject();
		JSONArray jsonTributosArray = new JSONArray();
		for(int i = 0; i < info.size(); i++) {

			JSONObject jsonApendiceItem = new JSONObject();
			jsonApendiceItem.put(EDocument.CAMPO, "Info");
			jsonApendiceItem.put(EDocument.ETIQUETA, "Descripcion");
			jsonApendiceItem.put(EDocument.VALOR, info.get(i));
			jsonTributosArray.put(jsonApendiceItem);
		}
		
		jsonApendice.put(CreditoFiscal.APENDICE, jsonTributosArray);
		return jsonApendice;
	}
	
	public String createNumeroControl(MInvoice invoice, MClient client) {
		String prefix = Optional.ofNullable(invoice.getC_DocType().getDefiniteSequence().getPrefix()).orElse("");
		String documentno = invoice.getDocumentNo().replace(prefix,"");
		String suffix = Optional.ofNullable(invoice.getC_DocType().getDefiniteSequence().getSuffix()).orElse("");	
		documentno = documentno.replace(suffix,"");
		String idIdentification  = StringUtils.leftPad(documentno, 15,"0");
		MPOS mpos = null;
		String pos = "";
		if (invoice.getC_POS_ID()>0) {
			mpos = (MPOS)invoice.getC_POS();
			pos = mpos.get_ValueAsString("ei_POS");
		}
		if(mpos == null)
		{
			mpos = new Query(invoice.getCtx(), MPOS.Table_Name, "AD_Org_ID=? ", trxName)
					.setParameters(invoice.getAD_Org_ID())
					.setOnlyActiveRecords(true)
					.setOrderBy("C_POS_ID")
					.first();
			if (mpos != null)
			pos = mpos.get_ValueAsString("ei_POS");
		}
		if(mpos == null){
			mpos = new Query(invoice.getCtx(), MPOS.Table_Name , "",  trxName)
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.setOrderBy("C_POS_ID")
					.first();
			if (pos != null)
			pos = mpos.get_ValueAsString("ei_POS");			
		}
		else {
			
		}
		MOrgInfo orgInfo = MOrgInfo.get(invoice.getCtx(), invoice.getAD_Org_ID(), invoice.get_TrxName());
		String idPosCompany = orgInfo.get_ValueAsString("ei_Sucursal") + pos;
		String numeroControl = "DTE-" + docType_getE_DocType((MDocType)invoice.getC_DocType()).getValue()
				+ "-"+ StringUtils.leftPad(idPosCompany, 8,"0") + "-"+ idIdentification;
		return numeroControl;
	}
	
	public String createCodigoGeneracion(MInvoice invoice) {
		
		String numControl = invoice.get_UUID().toUpperCase();
		return numControl;		
	}
	
	public X_E_DocType  docType_getE_DocType(MDocType docType) {
		X_E_DocType e_docType = new X_E_DocType(Env.getCtx()	, docType.get_ValueAsInt(X_E_DocType.COLUMNNAME_E_DocType_ID), null);
		return e_docType;
	}
	
	


}
