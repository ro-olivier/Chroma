package fr.zigomar.chroma.chroma.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.SettingsActivity;

public class ExportDataTask extends AsyncTask<Object, Void, Integer> {

    private static final Integer ExportKO_InvalidNumberOfArguments = -4;
    private static final Integer ExportKO_EncryptionParamWriteFailed = -3;
    private static final Integer ExportKO_NOPWDProvided = -2;
    private static final Integer ExportKO = -1;
    private static final Integer ExportOKWithEncryption = 2;
    private static final Integer ExportOK = 1;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public ExportDataTask(Context c) {
        this.context = c;
    }

    @Override
    protected Integer doInBackground(Object... params) {

        JSONArray data = new JSONArray();
        Pattern validDataFilename = Pattern.compile("((?:19|20)\\d\\d)-(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01]).json");

        int beginDateInt = 0;
        int endDateInt = 0;

        String beginDate;
        String endDate;

        String exportFilename;

        switch (params.length) {
            case 0:
                Log.i("CHROMA", "Full Export was called");
                exportFilename = context.getString(R.string.FullExportFilename);
                break;

            case 2:
                Log.i("CHROMA", "exportOnReceiveExportDates was called");

                beginDate = (String) params[0];
                endDate = (String) params[1];

                beginDateInt = dataFilenameFromStringToInt(beginDate);
                endDateInt = dataFilenameFromStringToInt(endDate);

                if (beginDateInt != endDateInt) {
                    exportFilename = "chroma_export_" + beginDate + "_" + endDate + ".json";
                } else {
                    exportFilename = "chroma_export_" + beginDate + ".json";
                }

                break;

            case 3:
                Log.i("CHROMA", "exportOnReceiveExportPeriod was called");

                beginDate = (String) params[0];
                endDate = (String) params[1];

                beginDateInt = dataFilenameFromStringToInt(beginDate);
                endDateInt = dataFilenameFromStringToInt(endDate);

                exportFilename = (String) params[2];
                break;

            default:
                return ExportKO_InvalidNumberOfArguments;
        }

        for (File f : context.getFilesDir().listFiles()) {
            if (f.isFile()) {
                String filename = f.getName();
                Matcher matcher = validDataFilename.matcher(filename);
                String filenameIntStr = filename.substring(0, 10);

                if (matcher.matches()) {
                int filenameInt = dataFilenameFromStringToInt(filenameIntStr);
                    if (params.length == 0 || filenameInt >= beginDateInt && filenameInt <= endDateInt) {
                        Log.i("CHROMA", "Processing " + filename);
                        try {
                            InputStream is = context.openFileInput(filename);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            int byte_read = is.read(buffer);
                            if (byte_read != size) {
                                Log.i("CHROMA", "Did not read the complete file, or something else went wrong");
                            }
                            is.close();
                            JSONObject temp = new JSONObject(new String(buffer, "UTF-8"));
                            temp.put("date", filenameIntStr);
                            data.put(temp);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("CHROMA", "Dismissing : " + filename);
                    }
                }
            }
        }

        return writeExportFile(data, exportFilename);
    }

    private Integer writeExportFile(JSONArray data, String filename) {

        Log.i("CHROMA", "Writing to : " + filename);

        Integer result = ExportKO;

        File exportFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), filename);

        FileOutputStream fostream = null;
        try {
            fostream = new FileOutputStream(exportFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CipherOutputStream cstream;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(SettingsActivity.KEY_PREF_ENC, false)) {

            String password = sharedPref.getString(SettingsActivity.KEY_PREF_PWD, "");
            if (password.length() == 0) {
                result = ExportKO_NOPWDProvided;
                //return le INT qui fait afficher ce Toast
                //Toast.makeText(context, R.string.ExportKO_NOPWDProvided, Toast.LENGTH_SHORT).show();
            } else {

                byte[] b = new byte[16];
                new Random().nextBytes(b);
                String iv_str = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);

                try {
                    final String keyGenAlgorithm = "PBKDF2WithHmacSHA256";
                    final String salt = "CHROMA_SALT";
                    final String cipherAlgorithm = "AES/GCM/NoPadding";

                    final SecretKeyFactory factory = SecretKeyFactory.getInstance(keyGenAlgorithm);
                    final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
                    final SecretKey tmp = factory.generateSecret(spec);
                    final SecretKey key = new SecretKeySpec(tmp.getEncoded(), cipherAlgorithm.split("/")[0]);
                    final IvParameterSpec IV = new IvParameterSpec(b);
                    final Cipher cipher;

                    cipher = Cipher.getInstance(cipherAlgorithm);
                    cipher.init(Cipher.ENCRYPT_MODE, key, IV);
                    cstream = new CipherOutputStream(fostream, cipher);
                    try {
                        cstream.write(data.toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            cstream.close();
                            try {
                                saveEncryptionParams(keyGenAlgorithm, salt, iv_str, cipherAlgorithm);
                            } catch (IOException e) {
                                result = ExportKO_EncryptionParamWriteFailed;
                            }
                            result = ExportOKWithEncryption;
                            // return le INT qui fait afficher ce Toast
                            // Toast.makeText(context, R.string.ExportOKWithEncryption, Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                    e.printStackTrace();
                }

            }
        } else {
            try {
                if (fostream != null) {
                    fostream.write(data.toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fostream != null) {
                        fostream.close();
                        result = ExportOK;
                        // return le INT qui fait afficher ce Toast
                        //Toast.makeText(getApplicationContext(), R.string.ExportOK, Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private void saveEncryptionParams(String keyGenAlgorithm, String salt, String iv, String cipherAlgorithm) throws IOException {
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(
                context.getResources().getString(R.string.EncryptionParamsFile), Context.MODE_PRIVATE);
        String data_to_file = "Encryption parameters for file exported and encrypted on " + new Date().toString() + "\n\n" +
                "keyGenAlgorithm = " + keyGenAlgorithm + "\n" +
                "salt = " + salt + "\n" +
                "cipherAlgorithm = " + cipherAlgorithm + "\n" +
                "iv = " + iv;
        outputStream.write(data_to_file.getBytes());
        outputStream.close();
    }

    @Override
    protected void onPostExecute(Integer result) {

        switch (result) {

            case -4:
                Toast.makeText(context, R.string.ExportKO_InvalidNumberOfArguments, Toast.LENGTH_SHORT).show();
                break;

            case -3:
                Toast.makeText(context, R.string.ExportKO_EncryptionParamWriteFailed, Toast.LENGTH_SHORT).show();
                break;

            case -2:
                Toast.makeText(context, R.string.ExportKO_NOPWDProvided, Toast.LENGTH_SHORT).show();
                break;

            case -1:
                Toast.makeText(context, R.string.ExportKO, Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(context, R.string.ExportOKWithEncryption, Toast.LENGTH_SHORT).show();
                break;

            case 1:
                Toast.makeText(context, R.string.ExportOK, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int dataFilenameFromStringToInt(String filename) {
        return Integer.parseInt(filename.split("-")[0] + filename.split("-")[1] + filename.split("-")[2]);
    }
}
