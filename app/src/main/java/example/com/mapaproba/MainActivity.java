package example.com.mapaproba;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity  {
    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginData();
            }
        });

    }

    private void getLoginData(){
        class GetLoginData extends AsyncTask<Void,Void,String> {
            String username = ed1.getText().toString(), password = ed2.getText().toString();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray result = jsonObject.getJSONArray("result");
                    if(result.length() == 1)
                    {
                        JSONObject c = result.getJSONObject(0);
                        String usernameJson = c.getString("user");
                        String passwordJson = c.getString("pass");
                        String idJson = c.getString("id");
                        if (ed1.getText().toString().equals(usernameJson) && ed2.getText().toString().equals(passwordJson)) {
                            //Toast.makeText(getApplicationContext(), "Uspjesno ste prijavljeni...", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                            i.putExtra("KORISNIK_ID",idJson);
                            startActivity(i);
                            //setContentView(R.layout.activity_maps);
                        }
                    }

                    else Toast.makeText(getApplicationContext(), "Netacni podaci", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam("http://192.168.0.101:80/getLogin.php?user=", username, password);
                return s;
            }
        }
        GetLoginData gld = new GetLoginData();
        gld.execute();
    }


}
