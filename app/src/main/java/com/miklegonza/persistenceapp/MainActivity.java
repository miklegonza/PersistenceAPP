package com.miklegonza.persistenceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
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

    private EditText edtId;
    private EditText edtName;
    private EditText edtLast;
    private Button btnInsert;
    private Button btnFind;
    private Button btnModify;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PersistenceApp);
        setContentView(R.layout.activity_main);

        init();
        listeners();
    }

    private void init() {
        this.edtId = findViewById(R.id.editId);
        this.edtId.requestFocus();
        this.edtName = findViewById(R.id.editName);
        this.edtLast = findViewById(R.id.editLast);
        this.btnInsert = findViewById(R.id.btnInsert);
        this.btnFind = findViewById(R.id.btnFind);
        this.btnModify = findViewById(R.id.btnModify);
        this.btnDelete = findViewById(R.id.btnDelete);
    }

    private void listeners() {
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
            hideKeyboard(this);
        }, error -> {
            Toast.makeText(MainActivity.this, error.getMessage() + "", Toast.LENGTH_LONG).show();
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideKeyboard(this);
        }, error -> {
            Toast.makeText(MainActivity.this, error.getMessage() + "", Toast.LENGTH_LONG).show();
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
            hideKeyboard(this);
        }, error -> {
            Toast.makeText(MainActivity.this, error.getMessage() + "", Toast.LENGTH_LONG).show();
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
            hideKeyboard(this);
        }, error -> {
            Toast.makeText(MainActivity.this, error.getMessage() + "", Toast.LENGTH_LONG).show();
        });
        Volley.newRequestQueue(this).add(deleteRequest);
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}