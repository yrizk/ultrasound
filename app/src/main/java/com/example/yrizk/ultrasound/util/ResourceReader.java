package com.example.yrizk.ultrasound.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceReader {
    /**
     * Reads in text from a resource file and returns a String containing the
     * text.
     */
    public static String readFromResource(Context context,
        int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream =
                context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not open resource: " + resourceId, e);
        } catch (NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }

        return body.toString();
    }
}
