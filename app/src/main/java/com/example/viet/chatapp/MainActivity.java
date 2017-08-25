package com.example.viet.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.viet.chatapp.FirebaseReferenceUtits.getChatRef;
import static com.example.viet.chatapp.FirebaseReferenceUtits.getImageRef;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int RC = 1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edtContent)
    EditText edtContent;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    private UserManager mUserManager = UserManager.getsInstance();
    private ArrayList<Chat> mArrChat = new ArrayList<>();
    private ChatRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showDisplayNameNameDialog();
        initViews();
        getChatRef().addChildEventListener(childEventListener);
    }

    private void initViews() {
        btnSend.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new ChatRecyclerViewAdapter(mArrChat);
//        mAdapter.setmOnItemBinded(new ChatRecyclerViewAdapter.OnItemBinded() {
//            @Override
//            public void onBinded(int size) {
//                recyclerView.smoothScrollToPosition(size - 1);
//            }
//        });
        recyclerView.setAdapter(mAdapter);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    mArrChat.add(chat);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    });

                }
            }.start();

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void showDisplayNameNameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter display name ");
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                editText.requestFocus();
                Button b = dialog.getButton(dialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(editText.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Display name must not empty", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            mUserManager.setmDisplayName(editText.getText().toString().trim());
                            editText.setHint(editText.getText().toString().trim() + " ...");
                            Toast.makeText(MainActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSend) {
            if (TextUtils.isEmpty(edtContent.getText().toString())) {
                Toast.makeText(this, "Content must not empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Chat chat = new Chat(edtContent.getText().toString().trim(), mUserManager.getmDisplayName(), "text");
            edtContent.setText("");
            getChatRef().push().setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "pushed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (id == R.id.ivPhoto) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, RC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC) {
            if (resultCode == RESULT_OK) {
                if (data == null || data.getData() == null) {
                    Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Uri imageUri = data.getData();
                getObsevableUpload(imageUri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<UploadTask>() {
                            @Override
                            public void accept(UploadTask o) throws Exception {
                                o.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Chat chat = new Chat(taskSnapshot.getDownloadUrl().toString(), mUserManager.getmDisplayName(), "image");
                                        getChatRef().push().setValue(chat);
                                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        }
    }

    private Observable<UploadTask> getObsevableUpload(final Uri imageUri) {
        return Observable
                .fromCallable(new Callable<UploadTask>() {
                    @Override
                    public UploadTask call() throws Exception {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte b[] = bos.toByteArray();
                        File file = new File(imageUri.getPath());
                        UploadTask uploadTask = getImageRef(file.getName()).putBytes(b);
                        return uploadTask;
                    }
                });
    }
}
