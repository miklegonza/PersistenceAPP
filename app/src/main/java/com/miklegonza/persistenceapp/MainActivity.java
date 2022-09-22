package com.miklegonza.persistenceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String baseURL = "http://192.168.0.12:3000/";

    EditText edtId;
    EditText edtName;
    EditText edtLast;
    Button btnInsert;
    Button btnFind;
    Button btnModify;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.Theme_PersistenceApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.edtId = findViewById(R.id.editId);
        this.edtName = findViewById(R.id.editName);
        this.edtLast = findViewById(R.id.editLast);
        this.btnInsert = findViewById(R.id.btnInsert);
        this.btnFind = findViewById(R.id.btnFind);
        this.btnModify = findViewById(R.id.btnModify);
        this.btnDelete = findViewById(R.id.btnDelete);

        this.btnInsert.setOnClickListener(view -> {
            insert(
                    edtId.getText().toString(),
                    edtName.getText().toString(),
                    edtLast.getText().toString()
            );
        });

        this.btnFind.setOnClickListener(view -> {
            find(edtId.getText().toString());
        });

        this.btnModify.setOnClickListener(view -> {
            update(
                    edtId.getText().toString(),
                    edtName.getText().toString(),
                    edtLast.getText().toString()
            );
        });

        this.btnDelete.setOnClickListener(view -> {
            delete(edtId.getText().toString());
        });
    }

    private void insert(String id, String name, String last) {
        String url = baseURL + "api";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(MainActivity.this, "Usuario insertado", Toast.LENGTH_LONG).show();
            edtId.setText("");
            edtName.setText("");
            edtLast.setText("");
        }, error -> {
            Log.e("Error", error.getMessage());
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("name", name);
                params.put("last", last);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    private void find(String id) {
        String uri = String.format(baseURL + "api/search?id=%1$s", id);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    if (jsonObject.getString("id").equals(id)) {
                        edtName.setText(jsonObject.getString("nombres"));
                        edtLast.setText(jsonObject.getString("apellidos"));
                    }
                }
                Toast.makeText(MainActivity.this, "Result: " + response, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("Error", error.getMessage());
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> query = new HashMap<>();
                query.put("id", id);

                return query;
            }
        };
        Volley.newRequestQueue(this).add(getRequest);
    }

    private void update(String id, String name, String last) {
        String uri = String.format(baseURL + "api/%1$s", id);

        StringRequest putRequest = new StringRequest(Request.Method.PUT, uri, response -> {
            Toast.makeText(MainActivity.this, "Usuario modificado", Toast.LENGTH_LONG).show();
            edtId.setText("");
            edtName.setText("");
            edtLast.setText("");
        }, error -> {
            Log.e("Error", error.getMessage());
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("last", last);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(putRequest);
    }

    private void delete(String id) {
        String uri = String.format(baseURL + "api/%1$s", id);

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, uri, response -> {
            Toast.makeText(MainActivity.this, "Usuario eliminado", Toast.LENGTH_LONG).show();
            edtId.setText("");
            edtName.setText("");
            edtLast.setText("");
        }, error -> Log.e("Error", error.getMessage() + ""));
        Volley.newRequestQueue(this).add(deleteRequest);
    }

}