package com.abd.lastandfinal;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    ListView myListview;
    List<Students> studentsList;

    DatabaseReference studentDbRef;
    private String name,rollno,mobileno,email, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().setTitle("View Data");
        myListview = findViewById(R.id.lv_list);
        studentsList = new ArrayList<>();

        studentDbRef = FirebaseDatabase.getInstance().getReference("Students");

        studentDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentsList.clear();

                for (DataSnapshot studentDatasnap : dataSnapshot.getChildren()){
                    Students students = studentDatasnap.getValue(Students.class);
                    studentsList.add(students);
                }

                ListAdapter adapter = new ListAdapter(ViewActivity.this,studentsList);
                myListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set itemLong listener on listview item

        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Students students = studentsList.get(position);
                showUpdateDialog(students.getId(), students.getName());

                return false;
            }
        });
    }

    private void showUpdateDialog(final String id, String name){

        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mDialogView = inflater.inflate(R.layout.update_dialog, null);

        mDialog.setView(mDialogView);

        //create views refernces
        final EditText etUpdateName = mDialogView.findViewById(R.id.etUpdateName);
        final EditText etUpdateRollno = mDialogView.findViewById(R.id.etUpdateRollno);
        final EditText editTextMobileNo = mDialogView.findViewById(R.id.etUpdateMobileNo);
        final EditText etUpdateEmail = mDialogView.findViewById(R.id.etUpdateEmail);
        final Spinner mSpinner = mDialogView.findViewById(R.id.updateSpinner);
        Button btnUpdate = mDialogView.findViewById(R.id.btnUpdate);
        Button btnDelete = mDialogView.findViewById(R.id.btnDelete);

        mDialog.setTitle("Updating " + name +" record");

        final AlertDialog alertDialog = mDialog.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here we will update data in database
                //now get values from view

                String newName = etUpdateName.getText().toString();
                String newRollno = etUpdateRollno.getText().toString();
                String newMobileno = editTextMobileNo.getText().toString();
                String newEmail = etUpdateEmail.getText().toString();
                String newCourse = mSpinner.getSelectedItem().toString();

                updateData(id,newName,newRollno,newMobileno,newEmail,newCourse);

                Toast.makeText(ViewActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(id);

                alertDialog.dismiss();
            }
        });
    }

    private void updateData(String id, String newName, String newRollno, String newMobileno, String newEmail, String newCourse) {
        //creating database reference
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Students").child(id);
        Students students = new Students(id, name, rollno,mobileno,email, course);
        DbRef.setValue(students);
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord(String id){
        //create reference to database
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Students").child(id);
        //we referencing child here because we will be delete one record not whole data data in database
        //we will use generic Task here so lets do it..

        Task<Void> mTask = DbRef.removeValue();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error deleting record");
            }
        });
    }



}