package cs201.project.afinal.thetraveler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cs201.project.afinal.thetraveler.model.User;


public class SignupActivity extends AppCompatActivity {

    public static User user;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("SIGNUP ACTIVITY", "NEW SIGNUP");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signUpButton = (Button)findViewById(R.id.signUp);
        Button signInButton = (Button) findViewById(R.id.signIn);
        Button guestSignInButton = (Button) findViewById(R.id.guestSignIn);
       // queue = Volley.newRequestQueue(this);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText fname = (EditText) findViewById(R.id.fName);
                String firstName = fname.getText().toString();

                EditText lname = (EditText) findViewById(R.id.lName);
                String lastName = lname.getText().toString();

                EditText emailText = (EditText) findViewById(R.id.email);
                String email = emailText.getText().toString();

                EditText passwordText = (EditText) findViewById(R.id.password);
                String password = passwordText.getText().toString();

                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                Log.e("SIGNUP", "NEW INTENT");

                //adds int to intent: signUp - 0
                intent.putExtra(HomeActivity.SIGNUP_KEY, 0);
                startActivity(intent);

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText fname = (EditText) findViewById(R.id.fName);
                String firstName = fname.getText().toString();

                EditText lname = (EditText) findViewById(R.id.lName);
                String lastName = lname.getText().toString();

                EditText emailText = (EditText) findViewById(R.id.email);
                String email = emailText.getText().toString();

                EditText passwordText = (EditText) findViewById(R.id.password);
                String password = passwordText.getText().toString();

                String requestUrl = "http://10.0.2.2:8080/csci201-fp-server/rest/user/email/" + email;
               // requestJSONParse(requestUrl);
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);

//                intent.putExtra("user", user);
                //signIn - 1
                intent.putExtra(HomeActivity.SIGNUP_KEY, 1);
                startActivity(intent);
            }
        });
        guestSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
              //  intent.putExtra("user", user);
                intent.putExtra(HomeActivity.SIGNUP_KEY, 2);
                startActivity(intent);
            }
        });

    }

    //TODO This will contact the Volley request for JSON data
    public void requestJSONParse(String reqURL) {
        //data comes back as an array
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, reqURL,
                null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("SIGNUP ACTIVITY", "response");
                            user = new User();
                            String email = response.getString("email");
                            String id = response.getString("id");
                            String name = response.getString("name");
                            String password = response.getString("password");
                            int score = response.getInt("score");
                            user.setScore(score);
                            user.setPassword(password);
                            user.setName(name);
                            user.setId(id);
                            user.setEmail(email);


                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        if(request == null)
            Log.e("TIMELINE FRAGMENT", "NULL REQUEST");
        queue.add(request);
    }

}
