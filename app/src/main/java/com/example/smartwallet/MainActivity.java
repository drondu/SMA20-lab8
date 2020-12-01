package com.example.smartwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    // ui
    private TextView tStatus;
    private EditText  eSearch, eIncome, eExpenses;
    private Spinner sSearch;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private ValueEventListener databaseListener;
    private String spinValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        sSearch = (Spinner) findViewById(R.id.eSpin);
        sSearch.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        final List<MothlyStruct> monthlyExpenses = new ArrayList<>();
        final List<String> monthNames = new ArrayList<>();

        // spinner adapter
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monthNames);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearch.setAdapter(sAdapter);


        databaseReference.child("calendar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monthNames.clear();
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // create a new instance of MonthlyExpense
//                        MothlyStruct inst = monthSnapshot.getValue(MothlyStruct.class);
                        String month = monthSnapshot.getKey();
                        // save the key as month name
                        Log.d("drondu: ", month);
                        if(!monthNames.contains("month"))
                            monthNames.add(month);
                        // save the month and month name

                    } catch (Exception e) {
                    }
                }

                // notify the spinner that data may have changed
                sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        currentMonth = sSearch.getSelectedItem().toString();

        tStatus.setText("Searching ...");
        createNewDBListener();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth = eSearch.getText().toString().toLowerCase();

                    tStatus.setText("Searching ...");
                    createNewDBListener();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:
                {
                    databaseReference.child("calendar").child(currentMonth).setValue(new MothlyStruct(currentMonth, Float.valueOf(eExpenses.getText().toString()), Float.valueOf(eIncome.getText().toString()))).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.d("drondu", String.valueOf(task.isSuccessful()));
                        }
                    });
                }
                break;
        }
    }

    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
                databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MothlyStruct monthlyExpense = dataSnapshot.getValue(MothlyStruct.class);
                // explicit mapping of month name from entry key
                assert monthlyExpense != null;
                monthlyExpense.month = dataSnapshot.getKey();

                eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}