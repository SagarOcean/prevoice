package com.developer.sagar.prevoice.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sagar.prevoice.Book;
import com.developer.sagar.prevoice.HomeActivity;
import com.developer.sagar.prevoice.PdfReader;
import com.developer.sagar.prevoice.ProfileDetails;
import com.developer.sagar.prevoice.R;
import com.developer.sagar.prevoice.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.SimpleArcLoader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ReadFragment extends Fragment {

    private Button uploadFile;
    private final int PICK_PDF_CODE = 99;
    private StorageReference pdfRef;
    private DatabaseReference pdfFilesRef;
    private String downloadUrl,fileName,fileSize;
    private SimpleArcLoader loader;
    private Uri pdfUri;
    RecyclerView pdfList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reading_fragment, container, false);
        uploadFile = view.findViewById(R.id.upload_pdf);
        loader = view.findViewById(R.id.pdf_loader);
        pdfList = view.findViewById(R.id.books_list);
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
                intentPDF.setType("application/pdf");
                intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intentPDF, "Select Picture"), PICK_PDF_CODE);
            }
        });
        pdfRef = FirebaseStorage.getInstance().getReference().child("Pdf Files");
        pdfFilesRef = FirebaseDatabase.getInstance().getReference().child("books");
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null) {

            pdfUri = data.getData();

            Cursor returnCursor =
                    getActivity().getContentResolver().query(pdfUri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName =returnCursor.getString(nameIndex);
            fileSize=Long.toString(returnCursor.getLong(sizeIndex));
            Toast.makeText(getActivity().getApplicationContext(), fileName+" Uploaded successfully", Toast.LENGTH_SHORT).show();
            uploadFileTodatabase();
            retrieveFiles();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFileTodatabase() {
        if (pdfUri == null) {
            Toast.makeText(getContext(), "Please select a pdf first", Toast.LENGTH_SHORT).show();

        } else {
            loader.setVisibility(View.VISIBLE);
            loader.start();
            StorageReference pdfUploadRef = pdfRef.child(fileName);
            final UploadTask uploadTask = pdfUploadRef.putFile(pdfUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    downloadUrl = pdfRef.getDownloadUrl().toString();
                    return pdfRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUrl = task.getResult().toString();
                        HashMap<String, Object> pdfMap = new HashMap<>();
                        pdfMap.put("openFile", downloadUrl);
                        pdfMap.put("fileName", fileName);
                        pdfMap.put("fileSize", fileSize);
                        pdfFilesRef.updateChildren(pdfMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            loader.stop();
                                            loader.setVisibility(View.GONE);
                                            Toast.makeText(getActivity().getApplicationContext(), fileName+" Uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });
        }

    }

    public static class FindPdfViewHolder extends RecyclerView.ViewHolder {
        TextView pdfName,pdfSize;
        ImageView pdfImage;

        public FindPdfViewHolder(@NonNull View item) {
            super(item);
            pdfName = item.findViewById(R.id.book_name);
            pdfImage = item.findViewById(R.id.book_image);
            pdfSize = item.findViewById(R.id.book_size);

        }
    }


    private void retrieveFiles(){
        FirebaseRecyclerOptions<Book> options = null;

        options = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(pdfFilesRef, Book.class).build();

        FirebaseRecyclerAdapter<Book, ReadFragment.FindPdfViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Book, FindPdfViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull FindPdfViewHolder findPdfViewHolder, int i, @NonNull final Book book) {
                        findPdfViewHolder.pdfName.setText(book.getPdfName());
                        findPdfViewHolder.pdfSize.setText(book.getPdfSize());
                        Drawable myDrawable = getResources().getDrawable(R.drawable.pdf_reader);
                        findPdfViewHolder.pdfImage.setImageDrawable(myDrawable);
                        findPdfViewHolder.pdfName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), PdfReader.class);
                                intent.putExtra("bookName", book.getPdfName());
                                Toast.makeText(getActivity().getApplicationContext(), "Opening document", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ReadFragment.FindPdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_books, parent, false);
                        ReadFragment.FindPdfViewHolder viewHolder = new ReadFragment.FindPdfViewHolder(view);
                        return viewHolder;
                    }

                };
        pdfList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}
