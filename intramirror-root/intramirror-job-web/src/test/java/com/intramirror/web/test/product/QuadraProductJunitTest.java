package com.intramirror.web.test.product;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.test.BaseJunit4Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


public class QuadraProductJunitTest extends BaseJunit4Test {
	
	@Autowired
	private QuadraSynProductMapping productMappingService ; 
	

//	public QuadraProductJunitTest(String name) {
//	}

	@Before  
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@After  
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
    @Test  
//    @Transactional   //标明此方法需使用事务  
//    @Rollback(true)  //标明使用完此方法后事务回滚,true时为回滚  
    public void testAssume(){
    	Map<String,Object> mqData = new HashMap<String, Object>();
    	Map<String,Object> data = new HashMap<String, Object>();
    	
        mqData.put("DATETIME", "30/05/201719, 06, 51");
        mqData.put("KEY", "5799-35");
        mqData.put("CODICE", 5799);
        mqData.put("TAGLIA", "35");
        mqData.put("DISPO", "0");
        mqData.put("BARCODEFORN", null);
        mqData.put("DATA_ARRIVO", "19/12/2016");
        mqData.put("CODICE_SAP", null);
        mqData.put("E_COMMERCE", "YES");
        mqData.put("IDGRSTAGIONE", 5);
        mqData.put("GRSTAGIONE_ITA", "PRIMAVERAESTATE");
        mqData.put("GRSTAGIONE_ENG", "SPRINGSUMMER");
        mqData.put("IDSTAGIONE", 175);
        mqData.put("STAGIONE_ITA", "PRIMAVERAESTATE2017");
        mqData.put("STAGIONE_ENG", "SS172017");
        mqData.put("IDBRAND", 412);
        mqData.put("BRAND", "Iro");
        mqData.put("SESSO", "Donna");
        mqData.put("SESSO_ENG", "Women");
        mqData.put("IDGRTIPO", 22);
        mqData.put("GRTIPO_ITA", "CALZATURE");
        mqData.put("GRTIPO_ENG", "SHOES");
        mqData.put("IDTIPO", 164);
        mqData.put("TIPO_ITA", "CALZDONNA");
        mqData.put("TIPO_ENG", "WOMEN'SSHOES");
        mqData.put("IDGRMOD", 16);
        mqData.put("GRMODELLO_ITA", "SCARPE");
        mqData.put("GRMODELLO_ENG", "SHOES");
        mqData.put("IDMOD", 367);
        mqData.put("MODELLO_ITA", "SANDALO");
        mqData.put("MODELLO_ENG", "SANDAL");
        mqData.put("ARTICOLO", "392315");
        mqData.put("DESCRIZIONE_CORTA", "SANDALOZEPPABIANCO");
        mqData.put("DESCRIZIONE_LUNGA_ITA", null);
        mqData.put("DESCRIZIONE_LUNGA_ENG", null);
        mqData.put("IDCOLORE", 2834);
        mqData.put("COLORE_ITA", "BIANCO");
        mqData.put("COLORE_ENG", "WHITE");
        mqData.put("COLORE_WEB", null);
        mqData.put("IDFONDO", null);
        mqData.put("FONDO", null);
        mqData.put("FONDO_ENG", null);
        mqData.put("IDMATERIALE", null);
        mqData.put("MATERIALE_ITA", null);
        mqData.put("MATERIALE_ENG", null);
        mqData.put("LISTINO", "355");
        mqData.put("SCONTO", "0");
        mqData.put("PREZZO_NETTO", "355");
        mqData.put("FOTO1", "Image_5799.jpg");
        mqData.put("FOTO2", "Image_5799_2.jpg");
        mqData.put("FOTO3", null);
        mqData.put("FOTO4", null);
        mqData.put("FOTO5", null);
        mqData.put("FOTO6", null);
        mqData.put("FOTO7", null);
        mqData.put("FOTO8", null);
        mqData.put("TAGS", "STELLAMCCARTNEY,392315");
        
        data.put("store_code", "QUADRA");
        data.put("api_configuration_id", 85);
        data.put("vendor_id", 14);
        data.put("product", mqData);
    	
        System.out.println("Test assume");  
        System.out.println(new Gson().toJson(mqData));
        Map<String,Object> resultMap = productMappingService.handleMappingAndExecute(new Gson().toJson(data));
        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
//        throw new RuntimeException("error create product fail");
    }  
    

}
