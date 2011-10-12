package de.friendscout.android.auth;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;


/**
 * @see http://www.androidpit.de/de/android/forum/thread/403716/AccountManager-dem-Geheimnis-auf-der-Spur
 * @see http://code.google.com/p/google-api-java-client/wiki/APIs#Google+_API
 * @see https://code.google.com/apis/explorer/#_s=plus&_v=v1
 * @author clorenz
 * @created on 12.10.2011
 */

public class AuthenticationDemoActivity extends Activity {
	protected static final String TAG="authdemo";
	/**
	 * @see http://code.google.com/intl/de/apis/gdata/faq.html#clientlogin
	 * @see https://developers.google.com/+/api/oauth
	 */
	protected static final String AUTH_TOKEN_TYPE="oauth2:https://www.googleapis.com/auth/plus.me";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Logger.getLogger("*").setLevel(Level.ALL);
        System.setProperty("log.tag.*", "ALL");
        
        try {
            AccountManager accountManager = AccountManager.get(this);
            Account[] accounts =
                    accountManager.getAccountsByType("com.google");

            Log.d(TAG, Arrays.toString(accounts));
            /* scheitert, da wir nicht authentifiziert sind! Log.d(TAG, "email="+accountManager.getUserData(acc, "email"));  */
            
            Bundle options = null;

            accountManager.getAuthToken(accounts[0], AUTH_TOKEN_TYPE, true, 
            		new AccountManagerCallback<Bundle>() {
            	public void run(AccountManagerFuture<Bundle> amf) {
            		try {
            			Bundle b = amf.getResult();
            			String token = b.getString(AccountManager.KEY_AUTHTOKEN);
            			
            			/* Hier haben wir das Token */
            			if ( token!=null && token.length()>0)
            				Log.d(TAG, token);
            			
            			/**
            			 * @see http://www.ditii.com/2011/09/20/google-apis-client-library-for-java-now-with-oauth-2-0-and-google-http-client-library-for-java-beta-released/
            			 * originally: http://www.facebook.com/notes/google-data/google-apis-client-library-for-java-now-with-oauth-20/232697813446701?ref=nf
            			 */
            			HttpTransport httpTransport = new NetHttpTransport();
            			
            			/**
            			 * @see http://w2davids.wordpress.com/android-json-parsing-made-easy-using-jackson/
            			 */
            			JsonFactory jsonFactory = new JacksonFactory();

            			//GoogleAccessProtectedResource requestInitializer =
            			//    new GoogleAccessProtectedResource(token, httpTransport,
            			//    jsonFactory, "foo", "bar", "knurz");

            			Plus plus = new Plus(httpTransport, jsonFactory);
            			plus.setOauthToken(token);
            			
            			Person profile = plus.people.get("me").execute();

            			Toast.makeText(AuthenticationDemoActivity.this, profile.getAboutMe(), 5000);
            			
            		} catch (Throwable t) {
            			Log.e(TAG, "Eek: "+t.getMessage(), t);
            		}
            	}
            	}, null);
        } catch ( Exception e) {
            Log.e(TAG, "Eek: "+e.getMessage(),e);
        }
    }
}