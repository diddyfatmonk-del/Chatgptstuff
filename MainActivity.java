package com.example.libambtester;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final int PICK_LIBRARY = 41;
    private TextView status;
    private File libraryFile;
    private boolean loadAttempted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 40, 32, 32);

        TextView title = new TextView(this);
        title.setText("libAMB Native Loader Test");
        title.setTextSize(24);
        root.addView(title);

        TextView warning = new TextView(this);
        warning.setText("Diagnostic only. Loading a native library runs its code inside this app. Use only a file you trust. This does not connect to or modify a game.");
        warning.setTextSize(16);
        warning.setPadding(0, 20, 0, 24);
        root.addView(warning);

        Button choose = new Button(this);
        choose.setText("1. Choose libAMB.so");
        root.addView(choose);

        Button load = new Button(this);
        load.setText("2. Attempt to load library");
        root.addView(load);

        status = new TextView(this);
        status.setTextSize(16);
        status.setPadding(0, 24, 0, 0);
        status.setText("Status: No library selected.");
        root.addView(status);

        choose.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_LIBRARY);
        });

        load.setOnClickListener(v -> attemptLoad());

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LIBRARY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                libraryFile = copyToPrivateStorage(uri);
                loadAttempted = false;
                status.setText("Selected: " + libraryFile.getName()
                    + "\nSize: " + libraryFile.length() + " bytes"
                    + "\nReady to attempt loading.");
            } catch (Exception e) {
                status.setText("Copy failed: " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
            }
        }
    }

    private File copyToPrivateStorage(Uri uri) throws IOException {
        File out = new File(getFilesDir(), "libAMB.so");
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream stream = new FileOutputStream(out)) {
            if (in == null) throw new IOException("Could not open selected file.");
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) stream.write(buffer, 0, count);
        }
        return out;
    }

    private void attemptLoad() {
        if (libraryFile == null || !libraryFile.exists()) {
            status.setText("Choose libAMB.so first.");
            return;
        }
        if (loadAttempted) {
            status.setText("A load attempt was already made. Restart the app before trying again.");
            return;
        }
        loadAttempted = true;
        try {
            System.load(libraryFile.getAbsolutePath());
            status.setText("LOAD SUCCEEDED\nThe Android runtime accepted the library in this test app. This does not prove it works with Animal Company.");
        } catch (UnsatisfiedLinkError e) {
            status.setText("LOAD FAILED — UnsatisfiedLinkError\n" + e.getMessage()
                + "\n\nThis often means a missing dependency, incompatible ABI, or unresolved native symbol.");
        } catch (SecurityException e) {
            status.setText("LOAD BLOCKED — SecurityException\n" + e.getMessage());
        } catch (Throwable e) {
            status.setText("LOAD FAILED — " + e.getClass().getName()
                + "\n" + e.getMessage());
        }
    }
}
