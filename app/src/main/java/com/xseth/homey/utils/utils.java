package com.xseth.homey.utils;

import android.content.Context;
import android.content.Intent;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.wearable.activity.ConfirmationActivity;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class utils {

    /**
     * Show ConfirmationActivity with success animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationSuccess(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Show ConfirmationActivity with failure animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationFailure(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Show ConfirmationActivity with open on phone animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationPhone(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Get encrypted outputstream from file
     * @param ctx ApplicationContext
     * @param alias String alias for Decryption Master key
     * @param path String path of file to read
     * @return encrypted outputstream for file content
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static OutputStream getEncryptedOutputStream(Context ctx, String alias, String path) throws
            GeneralSecurityException, IOException {

        MasterKey masterKey = utils.getMasterKey(ctx, alias);

        EncryptedFile encryptedFile = new EncryptedFile.Builder(
                ctx,
                new File(path),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build();

        return encryptedFile.openFileOutput();
    }

    /**
     * Get encrypted inputstream from file
     * @param ctx ApplicationContext
     * @param alias String alias for Encryption Master key
     * @param path String path of where to write file
     * @return encrypted inputstream for file
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static InputStream getEncryptedInputStream(Context ctx, String alias, String path) throws
            GeneralSecurityException, IOException {

        MasterKey masterKey = utils.getMasterKey(ctx, alias);

        EncryptedFile encryptedFile = new EncryptedFile.Builder(
                ctx,
                new File(path),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        return encryptedFile.openFileInput();
    }

    /**
     * Get a MasterKey with alias
     *
     * If Masterkey does not exists, one is generated. MasterKey used for AES 256 GCM encryption
     * @param ctx ApplicationContext
     * @param alias String alias for Master key
     * @return a Master Key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static MasterKey getMasterKey(Context ctx, String alias) throws GeneralSecurityException
            , IOException {

        // Use SPEC MasterKeys.AES256_GCM_SPEC
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build();

        return new MasterKey.Builder(ctx)
                .setKeyGenParameterSpec(spec)
                .build();
    }
}
