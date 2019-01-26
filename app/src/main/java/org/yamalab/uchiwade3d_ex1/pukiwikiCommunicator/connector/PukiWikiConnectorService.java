package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/* */
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
/* 
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
*/
import org.yamalab.uchiwade3d_ex1.Util;
import org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language.HtmlTokenizer;
import org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language.InterpreterInterface;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

public class PukiWikiConnectorService implements Runnable, InterpreterInterface
{
	private static final String TAG = "PukiWikiConService";
	private String baseUrl;
	private String pageName;
	private String pageCharSet;
	private String charset;

    private Properties setting;
	private String messageTextArea;
	private boolean showMessage;
	private String urlField;
	private boolean authInputFlag;
	private boolean loginButtonPressed;
	private PukiwikiJavaApplication application;
    private String loginId;
    private String loginPassword;
	boolean authDialog=false;
	boolean authDialogBack=false;
	InterpreterInterface serviceInterface;

	public PukiWikiConnectorService(PukiwikiJavaApplication service, 
			       InterpreterInterface si) {
		Log.d(TAG,"PukiWikiConnectorService");
		this.application=service;
		this.println("saveButtonDebugFrame");
		charset="UTF-8";
		this.setting=new Properties();
		this.serviceInterface=si;
	}
	
	private void saveButtonActionPerformed() {
//		System.out.println("saveButton.actionPerformed, event="+evt);
		//TODO add your code for saveButton.actionPerformed
		Log.d(TAG,"saveButtonActionPerformed");
		this.editPageButtonActionPerformed();
		this.updateButtonActionPerformed();
	}
	private void saveText(String x){
		Log.d(TAG,"saveText: "+x);
		this.editPageButtonActionPerformed();
		this.replaceTextWith(x);
	}
	private void saveText(String url, String xx){
		Log.d(TAG,"saveText: editUrl="+url+" text="+xx+".\n");
		this.urlField=url;
		String x=connectToURL(url);
		if(x==null){
			this.application.sendCommandToActivity("activity show connector", "");
			this.application.sendCommandToActivity("connector setMessage", "confirm url and login, pw");
			println("connect error. when writing.");
			return;
		}
		
		// extract charSet from <?xml= ...?> which contains charSet;
		String firstXmltag=getBetween(x,"<?xml","?>");
		if(firstXmltag==null) {
			println("firstXmlTag==null");
			return;
		}
		this.pageCharSet=this.getTagProperty(firstXmltag,"encoding");
		if(this.pageCharSet==null)
			this.pageCharSet="UTF-8";
		this.println("pageCharSet="+this.pageCharSet);
		x=getBetween(x,"<body>","</body>");
		
		String headerPart=getBetween(x,"<div id=\"header\">","</div>");
		String pageNamePart=getBetween(headerPart,"<span class=\"small\">","</span>");
		StringTokenizer st=new StringTokenizer(pageNamePart,"?");
		this.baseUrl=st.nextToken();
		this.pageName=st.nextToken();
		this.editPageButtonActionPerformed();
		this.replaceTextWith(xx);
	}
	
	private String getUrlWithoutParameters(String url){
		if(url==null) return null;
		int i=url.indexOf("?");
		if(i<0) return url;
		String rtn=url.substring(0,i);
		return rtn;
	}
	
//	HttpClient client=null;
	DefaultHttpClient client=null;
	
	String currentUrl;
	private String connectToURL(String url) {
		Log.d(TAG,"connectToURL("+url+")");
		currentUrl=url;
		//TODO add your code for connectButton.actionPerformed
		String pageText=null;
//		client=new HttpClient();
		client=new DefaultHttpClient();
//		this.messageTextArea.append(url+"\n");
		if(this.authDialog){
     	   return this.connectToURLWithAuth(url);
 		}
		try{
			this.println("new getMethod("+url+")");
//    		HttpMethod method = new GetMethod(url);
			HttpGet method = new HttpGet(url);

//	    	method.getParams().setContentCharset("UTF-8");
//			int status=client.executeMethod(method);
			pageText= client.execute(
					method,
					new ResponseHandler<String>(){
				        @Override
				        public String handleResponse(HttpResponse response)
				                throws ClientProtocolException, IOException {
				            // response.getStatusLine().getStatusCode()�Ń��X�|���X�R�[�h�𔻒肷��B
				            // ����ɒʐM�ł����ꍇ�AHttpStatus.SC_OK�iHTTP 200�j�ƂȂ�B
				            switch (response.getStatusLine().getStatusCode()) {
				            case HttpStatus.SC_OK:
				                // ���X�|���X�f�[�^�𕶎���Ƃ��Ď擾����B
				                // byte[]�Ƃ��ēǂݏo�������Ƃ���EntityUtils.toByteArray()���g���B
				                return EntityUtils.toString(response.getEntity(), "UTF-8");
				            case HttpStatus.SC_NOT_FOUND:
				                throw new RuntimeException("�f�[�^�Ȃ���I"); //FIXME
				            
				            default:
				            	String status=(response.getStatusLine()).toString();
			    		        println("Method failed: " + status);
			    		        if(status.indexOf("401")>=0){
			    		      	     application.sendCommandToActivity("connector loginRequired", "");
			    		      	     application.sendCommandToActivity("connector setLoginMessage",  "Login:"+ currentUrl);
			                         println("connectToUrl("+currentUrl+")...Method faile"+status);
			    		        }
				                throw new RuntimeException(status); //FIXME
				            }

				        }
				    }
						
				);
			return pageText;
			/*
		    if (status != HttpStatus.SC_OK) {
		        this.println("Method failed: " + method.getStatusLine());
		        if((method.getStatusLine()).toString().indexOf("401")>=0){
		      	     application.sendCommandToActivity("connector loginRequired", "");
		      	     application.sendCommandToActivity("connector setLoginMessage",  "Login:"+ currentUrl);
                     println("connectToUrl("+url+")...Method faile"+status);
                      return null;
		        }
		    }
		    else{
		    	pageText=this.getText(method);
				return pageText;
		    }
		    */
		}
		catch(Exception e){
			this.println(e.toString()+"\n");
			e.printStackTrace();
		}
		return null;
	}
	String urlPage;
	private String connectToURLWait(String url) {
		urlPage=null;
		AsyncTask t=new connectToURLTask().execute(url);
		while(urlPage==null){
			try{
				Thread.sleep(50L);
			}
			catch(InterruptedException e){}
		}
		if(urlPage.equals("error")) return null;
		return urlPage;
	}

	private class connectToURLTask extends AsyncTask<String, Integer, Long> {
	     protected Long doInBackground(String... params ) {
	    	Log.d(TAG, "connectToURLTask doInBackground - " + params[0]);
	    	try{
    	    	urlPage=connectToURL(params[0]);
    	    	if(urlPage==null){
    	    		urlPage="error";
    	    	}
	    	}
	    	catch(Exception e){
	    		urlPage="error";
	    	}
	    	return 1L;
	     }
	}
	private String connectToURLWithAuth(String url) {
		Log.d(TAG,"connectToURLWithAuth("+url+")");
		//TODO add your code for connectButton.actionPerformed
   	    this.authDialog=true;
  	    this.currentUrl=url;
		String urlWithoutParameters=getUrlWithoutParameters(currentUrl);
		if(urlWithoutParameters==null){
			application.sendCommandToActivity("connector setMessage", "connect failed. no Url is specified.");			
		}
		if(!urlWithoutParameters.endsWith("index.php")){
			urlWithoutParameters=urlWithoutParameters+"index.php";
		}
		Log.d(TAG,"connectToURLWithAuth setProperty(\"auth-url\" - "+urlWithoutParameters);
  	    this.setting.setProperty("auth-url", urlWithoutParameters);
  	    application.sendCommandToActivity("connector setAuthUrl-",urlWithoutParameters);
	    String authUrl="basicAuth-"+urlWithoutParameters;
	    Log.d(TAG,"connectToURL-authUrl="+authUrl);
	    String idPass=setting.getProperty(authUrl);
	    Log.d(TAG,"connectToURL-idPass="+idPass);
       	String id="";
       	String pas="";
	    if(idPass!=null){
	        Log.d(TAG,"connectToURLWithAuth-login..idPass!=null");
	    	int bp=idPass.indexOf("::::");
	       	id=idPass.substring(0,bp);
	    	Log.d(TAG,"connectToURLWithAuth-login..id="+id);
	       	pas=idPass.substring(bp+"::::".length());
	    	Log.d(TAG,"connectToURLWithAuth-login..pass="+pas);
       	    if(id.equals("")){
				application.sendCommandToActivity("connector setMessage", "connect failed. no login id.");
	            return null;
	        }
	        if(pas.equals("")){
				application.sendCommandToActivity("connector setMessage", "connect failed. no password.");
	            return null;
	        }
	    }
	    else{
			application.sendCommandToActivity("connector setMessage", "connect failed. no id, password.");
			this.authDialog=false;
	        return null;
	    }
		this.println("authDialog is not null");
		String registeredUrl=setting.getProperty("auth-url");
		this.println("urlWithoutParamaters="+urlWithoutParameters);
		this.println("registeredUrl="+registeredUrl);
		this.println("authDialog is not null");
		String pageText=null;
		if(registeredUrl==null){
			this.application.sendCommandToActivity("connector setMessage", "No login Id, pass.");
			return null;
		}
		if(registeredUrl.equals(urlWithoutParameters)){
			this.println("registeredUrl == urlWithoutParameters");
//			client.getParams().setAuthenticationPreemptive(true);
		    // �F�؏��(���[�U���ƃp�X���[�h)�̍쐬.
	        Log.d(TAG,"connectToURLWithAuth, setProperty("+authUrl+" - "+idPass);
    	    this.setting.setProperty(authUrl,idPass);
		}
//		HttpMethod method=null;
		HttpGet method=null;
		try{
			Credentials defaultcreds1 = new UsernamePasswordCredentials(id,pas);
		    // �F�؂̃X�R�[�v.
	        AuthScope scope1 = new AuthScope(null, -1, null);
		    // �X�R�[�v�ƔF�؏��̑g�������Z�b�g.
//	    client.getState().setCredentials(scope1, defaultcreds1);				
			this.println("new getMethod("+url+")");
//    		method = new GetMethod(url);
//        	method.setDoAuthentication(true);
			client.getCredentialsProvider().setCredentials(
					scope1, 
					defaultcreds1);
    	}
   		catch(Exception e){
   			this.println(e.toString()+"\n");
   			e.printStackTrace();
   		}
//	    	method.getParams().setContentCharset("UTF-8");
		try{
//		    int status=client.executeMethod(method);
			method = new HttpGet(url);
/*
 			HttpResponse status=client.execute(method);
		    if (status != HttpStatus.SC_OK) {
		          this.println("Method failed: " + method.getStatusLine());
		          if((method.getStatusLine()).toString().indexOf("401")>=0){
		     			this.println("auth login...error..");		        	  
		          }

		    }
	    	pageText=this.getText(method);
			return pageText;
			*/
			pageText= client.execute(
					method,
					new ResponseHandler<String>(){
				        @Override
				        public String handleResponse(HttpResponse response)
				                throws ClientProtocolException, IOException {
				            // response.getStatusLine().getStatusCode()�Ń��X�|���X�R�[�h�𔻒肷��B
				            // ����ɒʐM�ł����ꍇ�AHttpStatus.SC_OK�iHTTP 200�j�ƂȂ�B
				            switch (response.getStatusLine().getStatusCode()) {
				            case HttpStatus.SC_OK:
				                // ���X�|���X�f�[�^�𕶎���Ƃ��Ď擾����B
				                // byte[]�Ƃ��ēǂݏo�������Ƃ���EntityUtils.toByteArray()���g���B
				                return EntityUtils.toString(response.getEntity(), "UTF-8");
				            case HttpStatus.SC_NOT_FOUND:
				                throw new RuntimeException("�f�[�^�Ȃ���I"); //FIXME
				            
				            default:
				            	String status=(response.getStatusLine()).toString();
			    		        println("Method failed: " + status);
			    		        if(status.indexOf("401")>=0){
			    		      	     application.sendCommandToActivity("connector loginRequired", "");
			    		      	     application.sendCommandToActivity("connector setLoginMessage",  "Login:"+ currentUrl);
			                         println("connectToUrl("+currentUrl+")...Method faile"+status);
			    		        }
				                throw new RuntimeException(status); //FIXME
				            }

				        }
				    }
						
				);
			}
		catch(Exception e){
   			this.println("auth login...executeMethod error"+e.toString()+"\n");
   			e.printStackTrace();			
   			return null;
		}
		this.messageTextArea=this.messageTextArea+pageText;
		return pageText;
	}

//	private String getText(HttpMethod method){
	/*
	private String getText(HttpGet method){
		String pageText="";
	    try{
		   InputStream is=method.getResponseBodyAsStream();
 		   InputStreamReader isr=new InputStreamReader(is,this.charset);
	       BufferedReader br=new BufferedReader(isr);
	       String line="";
	       pageText="";
	       while(true){
	    	   line=br.readLine();
	    	   pageText=pageText+line+"\n";
	        	if(line==null) break;
	    	    this.messageTextArea=this.messageTextArea+ line+"\n";
//	    	    System.out.println(line);
	       }
	       method.releaseConnection();		
	    }
	    catch(Exception e){}
	    return pageText;
	}
*/	
	private void sendButtonActionPerformed() {
//		this.println("sendButton.actionPerformed, event="+evt);
		//TODO add your code for sendButton.actionPerformed
	}
	String updateText;
	String actionUrl;
	String editCmd;
	String editPage;
	String editDigest;
	String editWriteValue;
	String editEncodeHint;
    String editUrlText;
	private void editPageButtonActionPerformed() {
		Log.d(TAG,"editPageButtonActionPerformed");
//		this.println("editPageButton.actionPerformed, event="+evt);
		//TODO add your code for editPageButton.actionPerformed
		   String editUrl=baseUrl+"?cmd=edit&page="+pageName;
		this.println("editUrl="+editUrl);
//		this.messageTextArea.append(baseUrl+"\n");
		this.println(baseUrl+"\n");
//		this.messageTextArea.append(editUrl+"\n");
		this.println(editUrl+"\n");
		editUrlText=editUrl;
        urlField=editUrlText;
   		messageTextArea="";
		String x=this.connectToURLWait(editUrl);
		if(x==null){
			this.application.sendCommandToActivity("activity showConnector", "");
			println("connect error.");
			return;
		}

//		String x=this.messageTextArea.getText();
		
		/* get the first form from the url*/
		String form=this.getBetween(x,"<form", "</form>");
		if(form==null) return;
		this.println("form="+form);
		/* get the head part in the text area from the form*/
		int i=form.indexOf("<textarea ");
		if(i<0) {
			this.println("Could not find out textarea");
			return;
		}
//		this.messageTextArea.setText("");
//		this.messageTextArea.append("i="+i+"\n");
		String y="";
		String z="";
		try{
		    y=form.substring(i);
		    z=y.substring(y.indexOf(">")+1);		
		}
		catch(Exception e){
			
		}
		this.println("z="+z);
//		this.println("plugInName="+this.plugInName);
//		int j=z.indexOf("#"+this.plugInName);
		int j=z.indexOf("result:");
		if(j<0){
			this.println("Could not find out result:");
			return;
		}
		int k=j+("result:").length();

		//String head=x.substring(0,k-1);
		this.println("j="+j+" k="+k);
		// has the command #netpaint argument? 
		String dataStart=z.substring(j);
		this.println("dataStart="+dataStart);
		/*
		if(dataStart.startsWith("result:")){
			String theCommand="result:" ;
			this.println("theCommand="+theCommand);
			k=j+theCommand.length();
		}
		*/
		this.println("j="+j+" k="+k);
		
		String head=z.substring(0,k);
		//
        head=head.replaceAll("&quot;", "\"");
        head=head.replaceAll("&lt;", "<");
        head=head.replaceAll("&gt;", ">");		
		//
		String w=z.substring(head.length());
		int l=w.indexOf("</textarea");
		String tail=w.substring(l);
		String body=w.substring(0,l-1);
		this.println("head="+head);
		this.println("body="+body);
		this.println("tail="+tail);
		this.updateText=head;
		String actionwork1=form.substring(0,form.indexOf(">"));
		this.println("actionwork1="+actionwork1);
		this.actionUrl=this.getTagProperty(actionwork1, "action");
		this.println("action url="+this.actionUrl);
		
		/* 
		 getting input properties
		 */
		HtmlTokenizer htmlt=new HtmlTokenizer(form);
		while(htmlt.hasMoreTokens()){
			String t=htmlt.nextToken();
			if(t.startsWith("<input")){
				String ttype=getTagProperty(t,"type");
				if(ttype.equals("hidden")){
				   String tname=getTagProperty(t,"name");
				   String tvalue=getTagProperty(t,"value");
				   this.println(" "+tname+"="+tvalue);
				   if(tname.equals("cmd")){
					   this.editCmd=tvalue;
				   }
				   if(tname.equals("page")){
					   this.editPage=tvalue;
				   }
				   if(tname.equals("digest")){
				   		this.editDigest=tvalue;
				   }
				   if(tname.equals("encode_hint")){
					   this.editEncodeHint=tvalue;
				   }
				}
				if(ttype.equals("submit")){
				   String tname=getTagProperty(t,"name");
				   String tvalue=getTagProperty(t,"value");
				   this.println(" "+tname+"="+tvalue);
				   if(tname.equals("write")){
						this.editWriteValue=tvalue;
			       }
				}
			}
		}
		
	}	
	private void updateButtonActionPerformed() {
		Log.d(TAG,"updateButtonActionPerformed");
		String output=application.getOutput();
		replaceTextWith(output);
	}
	String insertSpaceAfterNewLine(String x){
		StringTokenizer st=new StringTokenizer(x,"\n",true);
		String rtn="";
		while(st.hasMoreTokens()){
			String t=st.nextToken();
			if(t.equals("\n")){
				rtn=rtn+t+" ";
			}
			else{
				rtn=rtn+t;
			}
		}
		return rtn;
	}
	
	private String getTagProperty(String tag, String key){
//		System.out.println("tag="+tag);
//		System.out.println("key="+key);
		StringTokenizer st=new StringTokenizer(tag," =",true);
		String t=st.nextToken();
//		System.out.println(" first token="+t);
		while(st.hasMoreTokens()){
			t=st.nextToken();
			if(t.equals(" ")){        // skip space
				while(t.equals(" ")){
					if(!st.hasMoreTokens()) return "";
					t=st.nextToken();
				}
			}
			String keyx=t;
//			System.out.println(" key?="+keyx);
			if(!st.hasMoreTokens()) return "";
			t=st.nextToken();
			if(t.equals(" ")){        // skip space
				while(t.equals(" ")){
					if(!st.hasMoreTokens()) return "";
					t=st.nextToken();
				}
			}
			if(t.equals("=")){
//				System.out.println("...=");
				if(!st.hasMoreTokens()) return "";
				t=st.nextToken();
				if(t.equals(" ")){
					if(!st.hasMoreTokens()) return "";
					t=st.nextToken();
				}
			    if(keyx.equals(key)){
//			    	System.out.println(" keyx="+key+" t="+t);
			    	if(t.startsWith("\"")){
			    			t=t.substring(1);
			    	}
			    	if(t.endsWith("\"")){
			    		t=t.substring(0,t.length()-1);
			    	}
			    	return t;
			    }
			}
			
		}
		return "";
	}
	
	private void replaceTextWith(String x){
		// System.out.println("updateButton.actionPerformed, event="+evt);
		//TODO add your code for updateButton.actionPerformed
		
		/* make the body (fig) */
		/*
		try{
			fig=new String(fig.getBytes("UTF-8"),  "UTF-8");
		}
		catch(Exception e){
			return;
		}
		*/
		this.updateText=this.updateText+"\n "+insertSpaceAfterNewLine(x);
		
		this.urlField=this.actionUrl;
		String url=this.urlField;
		this.println("url="+url);
//		this.messageTextArea.append(this.updateText);
		   BufferedReader br = null;
//		System.out.println("updateText="+this.updateText);
		this.println("editWriteValue="+this.editWriteValue);
		this.println("editCmd="+this.editCmd);
		this.println("editPage="+this.editPage);
		this.println("digest="+this.editDigest);
		try{
//    		PostMethod method = new PostMethod(url);
			HttpPost method = new HttpPost(url);
    		if(this.client==null) return;
//    		method.getParams().setContentCharset(this.pageCharSet);
    		/*
    		method.setParameter("msg",this.updateText);
//    		method.setParameter("encode_hint",this.editEncodeHint);
    		method.addParameter("write",this.editWriteValue);
    		method.addParameter("cmd",this.editCmd);
    		method.addParameter("page",this.editPage);
    		method.addParameter("digest",this.editDigest);
    		*/
    		List<NameValuePair> params = new ArrayList<NameValuePair>();
    		params.add(new BasicNameValuePair("msg", this.updateText));
    		params.add(new BasicNameValuePair("encode_hint", this.editEncodeHint));
    		params.add(new BasicNameValuePair("write", this.editWriteValue));
    		params.add(new BasicNameValuePair("cmd", this.editCmd));
    		params.add(new BasicNameValuePair("page", this.editPage));
    		params.add(new BasicNameValuePair("digest", this.editDigest));
    		method.setEntity(new UrlEncodedFormEntity(params));


//			int status=client.executeMethod(method);
    		/*
    		int status=client.execute(method);
		    if (status != HttpStatus.SC_OK) {
		          this.println("Method failed: " + method.getStatusLine());
		          method.getResponseBodyAsString();
		    }
		    else {
		          br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
		          String readLine;
		          while(((readLine = br.readLine()) != null)) {
		            this.println(readLine);
		          }
		   }
		   */
		   String status= client.execute(
					method,
					new ResponseHandler<String>(){
			     		BufferedReader br = null;
				        @Override
				        public String handleResponse(HttpResponse response)
				                throws ClientProtocolException, IOException {
				            // response.getStatusLine().getStatusCode()�Ń��X�|���X�R�[�h�𔻒肷��B
				            // ����ɒʐM�ł����ꍇ�AHttpStatus.SC_OK�iHTTP 200�j�ƂȂ�B
				            switch (response.getStatusLine().getStatusCode()) {
				            case HttpStatus.SC_OK:
				                // ���X�|���X�f�[�^�𕶎���Ƃ��Ď擾����B
				                // byte[]�Ƃ��ēǂݏo�������Ƃ���EntityUtils.toByteArray()���g���B
				            	InputStream content = response.getEntity().getContent();
						          br = new BufferedReader(new InputStreamReader(content));
						          String readLine;
						          while(((readLine = br.readLine()) != null)) {
//						            println(readLine);
						          }
						          content.close();
						          br.close();
						          return response.getStatusLine().toString();
//				                return EntityUtils.toString(response.getEntity(), "UTF-8");
				            case HttpStatus.SC_NOT_FOUND:
				                throw new RuntimeException("�f�[�^�Ȃ���I"); //FIXME
				            
				            default:
				            	String status=(response.getStatusLine()).toString();
			    		        println("Method failed: " + status);
			    		        if(status.indexOf("401")>=0){
			    		      	     application.sendCommandToActivity("connector loginRequired", "");
			    		      	     application.sendCommandToActivity("connector setLoginMessage",  "Login:"+ currentUrl);
			                         println("connectToUrl("+currentUrl+")...Method faile"+status);
			    		        }
				                throw new RuntimeException(status); //FIXME
				            }

				        }
				    }
						
				);
//			method.releaseConnection();
		   client.getConnectionManager().shutdown();
		}
		catch(Exception e){
//			this.messageTextArea.append(e.toString()+"\n");
			System.out.println(""+e);
			e.printStackTrace();
		}
		
	}
	
	boolean isInStringConst(String x, int p){
		
		int px=0;
		int py=0;
		boolean isIn=false;
		while(px<x.length()){
			if(px>p) return false;
			char cx=x.charAt(px);
			char cy=0;
			py=px+1;
			if(cx=='"'){
				isIn=true;
				while(py<x.length()){
					cy=x.charAt(py);
					if(cy=='"'){
						if(px<p && p<py)
							return true;
						else{
							isIn=false;
							px=py;
							break;
						}
					}
					if(cy=='\\'){
						py=py+1;
					}
					py=py+1;
				}
				if(isIn)
					return true;
			}
			if(cx=='\\'){
				px=px+1;
			}
			px=px+1;
		}
		return false;
	}
	private Handler handler = new Handler(Looper.getMainLooper());
	String inputText;
	String readUrl;
	private void readFromPukiwikiPageAndSetData(String url){
		Log.d(TAG,"readFromPukiwikiPageAndSetData("+url+")");
		readUrl=url;
		/*
		this.println("editUrl="+url+"\n");
		this.urlField=url;
		String x=connectToURL(url);
		if(x==null){
			this.application.sendCommandToActivity("activity showConnector", "");
			this.application.sendCommandToActivity("connector setMessage","connection error. confirm url and login");
			println("connect error. when reading.");
			return;
		}
		
		// extract charSet from <?xml= ...?> which contains charSet;
		String firstXmltag=getBetween(x,"<?xml","?>");
		if(firstXmltag==null) {
			println("firstXmlTag==null");
			return;
		}
		this.pageCharSet=this.getTagProperty(firstXmltag,"encoding");
		if(this.pageCharSet==null)
			this.pageCharSet="UTF-8";
		this.println("pageCharSet="+this.pageCharSet);
		x=getBetween(x,"<body>","</body>");
		
		// exclude until <applet
//		int i=x.indexOf("<applet");
//		if(i<0) return;
//		x=x.substring(i);
		String headerPart=getBetween(x,"<div id=\"header\">","</div>");
		String pageNamePart=getBetween(headerPart,"<span class=\"small\">","</span>");
		StringTokenizer st=new StringTokenizer(pageNamePart,"?");
		this.baseUrl=st.nextToken();
//		System.out.println("baseUrl="+baseUrl);
		this.pageName=st.nextToken();
//		System.out.println("pageName="+pageName);
		// extract <pre>...</pre> where the figure is.
		String inw=getBetween(x,"<pre>","</pre>");
		String input=inw;
		if(input==null) return;
        input=input.replaceAll("&quot;", "\"");
        input=input.replaceAll("&lt;", "<");
        input=input.replaceAll("&gt;", ">");
        inputText=input;
        application.setInput(inputText+"\n");  
        */
		/*
		try{
		new readWriteWikiTask().execute(url);		
		}
		catch(Exception e){
			Log.d(TAG,"error.."+e.toString());
		}
		*/

        try{
		   handler.post(new Runnable() {
		      public void run() {
		          new readWriteWikiTask().execute(readUrl);
		      }
		   });
		 }
         catch(Exception e){
 			Log.d(TAG,"error.."+e.toString());        	 
         }
	}
    boolean accessingWebNow=false;	
	private class readWriteWikiTask extends AsyncTask<String, Integer, Long> {
		     protected Long doInBackground(String... params ) {
		    	Log.d(TAG, "doInBackground - " + params[0]);
		    	String url=params[0];
		    	accessingWebNow=true;

				Log.d(TAG,"readFromPukiwikiPageAndSetData("+url+")");
				println("editUrl="+url+"\n");
				urlField=url;
				String x=connectToURL(url);
				if(x==null){
					application.sendCommandToActivity("activity showConnector", "");
					application.sendCommandToActivity("connector setMessage","connection error. confirm url and login");
					println("connect error. when reading.");
					return 0L;
				}
				
				// extract charSet from <?xml= ...?> which contains charSet;
				String firstXmltag=getBetween(x,"<?xml","?>");
				if(firstXmltag==null) {
					println("firstXmlTag==null");
					return 0L;
				}
				pageCharSet=getTagProperty(firstXmltag,"encoding");
				if(pageCharSet==null)
					pageCharSet="UTF-8";
				println("pageCharSet="+pageCharSet);
				x=getBetween(x,"<body>","</body>");
				
				// exclude until <applet
//				int i=x.indexOf("<applet");
//				if(i<0) return;
//				x=x.substring(i);
				String headerPart=getBetween(x,"<div id=\"header\">","</div>");
				String pageNamePart=getBetween(headerPart,"<span class=\"small\">","</span>");
				StringTokenizer st=new StringTokenizer(pageNamePart,"?");
				baseUrl=st.nextToken();
//				System.out.println("baseUrl="+baseUrl);
				pageName=st.nextToken();
//				System.out.println("pageName="+pageName);
				// extract <pre>...</pre> where the figure is.
				String inw=getBetween(x,"<pre>","</pre>");
				String input=inw;
				if(input==null) return 0L;
		        input=input.replaceAll("&quot;", "\"");
		        input=input.replaceAll("&lt;", "<");
		        input=input.replaceAll("&gt;", ">");
		        inputText=input;
		        application.setInput(inputText+"\n"); 
		        return 1L;
		     }
		     protected void onPostExecute(Long result) {
		    	    Log.d(TAG, "onPostExecute - " + result);
		    	    accessingWebNow=false;
		     }

    }

	/*
	 *  get the first string which is in from l to r in the x
	 */
	String getBetween(String x, String l, String r){
//		System.out.println("x="+x);
		this.println("l="+l);
		this.println("r="+r);
		int i=0;
		while(i<=0){
			i=x.indexOf(l,i);
    		if(i<0) return null;
    		if(i==0) break;
	    	if(isInStringConst(x,i)){
		    	i=i+l.length();
		    }
		}
		
		i=i+l.length();
		int j=i;
		while(j<=i){
		    j=x.indexOf(r,j);
		    if(j<0) return null;
		    if(isInStringConst(x,j)){
		    	j=j+r.length();
		    }
		}
		String rtn="";
		try{
			rtn=x.substring(i,j);
		}
		catch(Exception e){
			return null;
		}
//		System.out.println("rtn="+rtn);
		return rtn;
	}
	private void println(String x){
		Log.d(TAG,"println("+x+")");
//		if(showMessage) return;
			/* */
		if(x.length()>300){
			x=x.substring(0,200)+"...";
		}
		if(this.application!=null){
				application.sendCommandToActivity("activity append message", x);
		}
//			this.messageTextArea.setCaretPosition((this.messageTextArea.getText()).length());
			/* */
//		System.out.println(x);
	}

	private void setUrl(String x){
		this.urlField=x;
	}
	String tempAuth;
	private class StringPair{
		public String x;
		public String y;
		public StringPair(String a, String b){
			x=a; y=b;
		}
	}
	int commandQueueMax=10;
	private Vector<StringPair> commandQueue=new Vector();
	public void parseCommand(String subcmd, String v){
		int cqs=commandQueue.size();
		if(cqs>commandQueueMax){
			return;
		}
        if(commandQueue!=null){
        	StringPair sp=new StringPair(subcmd,v);
        	commandQueue.addElement(sp);
        }
	}
	private boolean parseQueuedCommand(String subcmd, String v){
		String [] rest=new String[1];
		String cmd=Util.skipSpace(subcmd);
		Log.d(TAG,"parseQueuedCommand-"+cmd);
		if(Util.parseKeyWord(cmd,"connect-",rest)){ // connector connect
//			String x=this.connectToURL(v);
			this.connectToURLWait(v);
//			if(x==null){
//				this.application.sendCommandToActivity("activity showConnector", "");
//			}
			return true;
		}	
		else
		if(Util.parseKeyWord(cmd,"login-",rest)){ // connector login
			this.loginButtonPressed=true;
			String baseUrl=getUrlWithoutParameters(this.urlField);
			if(baseUrl==null){
				return false;
			}
			if(!baseUrl.endsWith("index.php")){
				baseUrl=baseUrl+"index.php";
			}
	        Log.d(TAG,"parseQueuedCommand, setProperty(\"auth-Url\" - "+baseUrl);
			this.setting.setProperty("auth-url",baseUrl);
		    String authUrl="basicAuth-"+baseUrl;
		    Log.d(TAG,"parseQueuedCommand-login, authUrl="+authUrl);
		    String id_pw=this.loginId+"::::"+this.loginPassword;
		    Log.d(TAG,"parseQueuedCommand-login, id-pw="+id_pw);
	        Log.d(TAG,"parseQueuedCommand, setProperty("+authUrl+" - "+id_pw);
			this.setting.setProperty(authUrl,id_pw);
			this.authDialog=true;
			this.readFromPukiwikiPageAndSetData(this.urlField);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"loginCancel-",rest)){
			this.loginButtonPressed=false;
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"message-",rest)){ // connector message
			this.println(v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"read-",rest)){ // connector read
			this.readFromPukiwikiPageAndSetData(this.urlField);
			return true;
		}		
		else
		if(Util.parseKeyWord(cmd,"readPage-",rest)){ // connector readPage
			String baseUrl=this.getUrlWithoutParameters(this.urlField);
			if(baseUrl==null){
				return false;
			}
			this.readFromPukiwikiPageAndSetData(baseUrl+"?"+v);
			return true;
		}		
		else
		if(Util.parseKeyWord(cmd,"replaceTextWith-",rest)){
			this.replaceTextWith(v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"saveText-",rest)){
			this.saveText(v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"saveTextAtUrl-",rest)){
			String xurl=rest[0];
			this.saveText(xurl, v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setShowMessage-",rest)){
			String x=v;
			if(x.equals("true"))
				this.showMessage=true;
			else
				this.showMessage=false;
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setLoginId-",rest)){
			this.loginId=v;
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setLoginPassword-",rest)){
			this.loginPassword=v;
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setIdPass-",rest)){
			if(this.tempAuth!=null)
//			this.setting.put(tempAuth, v);
			this.setting.setProperty(tempAuth, v);
			Log.d(TAG,"parseQueuedCommand setProperty("+tempAuth+" - "+v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setAuthUrl-",rest)){
			Log.d(TAG,"parseQueuedCommand setProperty(\"auth-Url\" - "+v);
//			this.setting.put("auth-Url", v);
//			this.setting.put("auth-url", v);
//			this.setting.setProperty("auth-url", v);
			String urlWithoutParameters=getUrlWithoutParameters(currentUrl);
			if(urlWithoutParameters==null){
				return false;
			}
//	  	  this.authDialog.setProperty("auth-url", urlWithoutParameters);
			if(!urlWithoutParameters.endsWith("index.php")){
				urlWithoutParameters=urlWithoutParameters+"index.php";
			}
			Log.d(TAG,"connectToURLWithAuth setProperty(\"auth-url\" - "+urlWithoutParameters);
			this.setting.setProperty("auth-url", urlWithoutParameters);

			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setAuth2Url-",rest)){
			this.tempAuth=v;
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"setUrl-",rest)){
			this.setUrl(v);
			return true;
		}
		return false;		
	}
    private Thread me;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		StringPair nextCommand=null;
		while(me!=null){
		   try{
			  int size=commandQueue.size();
			  if(size>0){
			     nextCommand=commandQueue.elementAt(0);
			     commandQueue.remove(0);
			     boolean rtn=parseQueuedCommand(nextCommand.x, nextCommand.y);
			     println(nextCommand.x+"-"+nextCommand.y+" rtn="+rtn);
			  }
			  else{
			     Thread.sleep(10);
			  }
		   }
		   catch(Exception e){
			
		   }
		}
	}
    public void start(){
    	Log.d(TAG,"start()");
    	if(me==null){
    		me=new Thread(this,"PukiWikiConnectorService");
    		me.start();
    	}
    }
    public void stop(){
    	Log.d(TAG,"stop()");
    	me=null;
    }

	@Override
	public EditText getOutputText() {
		// TODO Auto-generated method stub
		return serviceInterface.getOutputText();
	}

	@Override
	public boolean isTracing() {
		// TODO Auto-generated method stub
		return serviceInterface.isTracing();
	}

	@Override
	public String parseCommand(String x) {
		// TODO Auto-generated method stub
		String rest[]=new String[1];
		x=Util.skipSpace(x);
		if(Util.parseKeyWord(x, "getpage ", rest)){
			String url=rest[0];
			return this.connectToURLWait(url);
		}
		return null;
	}

	@Override
	public InterpreterInterface lookUp(String x) {
		// TODO Auto-generated method stub
		return serviceInterface.lookUp(x);
	}
}
