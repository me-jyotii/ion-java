// Copyright (c) 2009-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion.impl;

import static com.amazon.ion.impl.UnifiedInputStreamX.makeStream;
import static com.amazon.ion.impl._Private_IonConstants.BINARY_VERSION_MARKER_SIZE;
import static com.amazon.ion.util.IonStreamUtils.isIonBinary;

import com.amazon.ion.IonCatalog;
import com.amazon.ion.IonException;
import com.amazon.ion.IonReader;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonTextReader;
import com.amazon.ion.IonValue;
import com.amazon.ion.util.IonStreamUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * NOT FOR APPLICATION USE!
 */
@SuppressWarnings("deprecation")
public final class _Private_IonReaderFactory
{
    public static final IonReader makeReader(IonSystem system,
                                             IonCatalog catalog,
                                             byte[] bytes)
    {
        return makeReader(system, catalog, bytes, 0, bytes.length);
    }

    public static IonReader makeSystemReader(IonSystem system, byte[] bytes)
    {
        return makeSystemReader(system, bytes, 0, bytes.length);
    }


    public static final IonReader makeReader(IonSystem system,
                                             IonCatalog catalog,
                                             byte[] bytes,
                                             int offset,
                                             int length)
    {
        try
        {
            UnifiedInputStreamX uis = makeUnifiedStream(bytes, offset, length);
            return makeReader(system, catalog, uis, offset);
        }
        catch (IOException e)
        {
            throw new IonException(e);
        }
    }

    public static IonReader makeSystemReader(IonSystem system,
                                             byte[] bytes,
                                             int offset,
                                             int length)
    {
        try
        {
            UnifiedInputStreamX uis = makeUnifiedStream(bytes, offset, length);
            return makeSystemReader(system, uis, offset);
        }
        catch (IOException e)
        {
            throw new IonException(e);
        }
    }


    public static final IonTextReader makeReader(IonSystem system,
                                                 IonCatalog catalog,
                                                 char[] chars)
    {
        return makeReader(system, catalog, chars, 0, chars.length);
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                   char[] chars)
    {
        UnifiedInputStreamX in = makeStream(chars);
        return new IonReaderTextSystemX(system, in);
    }


    public static final IonTextReader makeReader(IonSystem system,
                                                 IonCatalog catalog,
                                                 char[] chars,
                                                 int offset,
                                                 int length)
    {
        UnifiedInputStreamX in = makeStream(chars, offset, length);
        return new IonReaderTextUserX(system, catalog, in, offset);
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                       char[] chars,
                                                       int offset,
                                                       int length)
    {
        UnifiedInputStreamX in = makeStream(chars, offset, length);
        return new IonReaderTextSystemX(system, in);
    }


    public static final IonTextReader makeReader(IonSystem system,
                                                 IonCatalog catalog,
                                                 CharSequence chars)
    {
        UnifiedInputStreamX in = makeStream(chars);
        return new IonReaderTextUserX(system, catalog, in);
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                   CharSequence chars)
    {
        UnifiedInputStreamX in = makeStream(chars);
        return new IonReaderTextSystemX(system, in);
    }


    public static final IonTextReader makeReader(IonSystem system,
                                                 IonCatalog catalog,
                                                 CharSequence chars,
                                                 int offset,
                                                 int length)
    {
        UnifiedInputStreamX in = makeStream(chars, offset, length);
        return new IonReaderTextUserX(system, catalog, in, offset);
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                   CharSequence chars,
                                                   int offset,
                                                   int length)
    {
        UnifiedInputStreamX in = makeStream(chars, offset, length);
        return new IonReaderTextSystemX(system, in);
    }


    public static final IonReader makeReader(IonSystem system,
                                             IonCatalog catalog,
                                             InputStream is)
    {
        try {
            UnifiedInputStreamX uis = makeUnifiedStream(is);
            return makeReader(system, catalog, uis, 0);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static IonReader makeSystemReader(IonSystem system,
                                             InputStream is)
    {
        try {
            UnifiedInputStreamX uis = makeUnifiedStream(is);
            return makeSystemReader(system, uis, 0);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }


    public static final IonTextReader makeReader(IonSystem system,
                                                 IonCatalog catalog,
                                                 Reader chars)
    {
        try {
            UnifiedInputStreamX in = makeStream(chars);
            return new IonReaderTextUserX(system, catalog, in);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                       Reader chars)
    {
        try {
            UnifiedInputStreamX in = makeStream(chars);
            return new IonReaderTextSystemX(system, in);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }


    public static final IonReader makeReader(IonSystem system,
                                             IonCatalog catalog,
                                             IonValue value)
    {
        return new IonReaderTreeUserX(value, catalog);
    }

    public static final IonReader makeSystemReader(IonSystem system,
                                                   IonValue value)
    {
        if (system != null && system != value.getSystem()) {
            throw new IonException("you can't mix values from different systems");
        }
        return new IonReaderTreeSystem(value);
    }


    //=========================================================================



    private static IonReader makeReader(IonSystem system,
                                        IonCatalog catalog,
                                        UnifiedInputStreamX uis,
                                        int offset)
        throws IOException
    {
        IonReader r;
        if (has_binary_cookie(uis)) {
            r = new IonReaderBinaryUserX(system, catalog, uis, offset);
        }
        else {
            r = new IonReaderTextUserX(system, catalog, uis, offset);
        }
        return r;
    }

    private static IonReader makeSystemReader(IonSystem system,
                                              UnifiedInputStreamX uis,
                                              int offset)
        throws IOException
    {
        IonReader r;
        if (has_binary_cookie(uis)) {
            // TODO pass offset, or spans will be incorrect
            r = new IonReaderBinarySystemX(system, uis);
        }
        else {
            // TODO pass offset, or spans will be incorrect
            r = new IonReaderTextSystemX(system, uis);
        }
        return r;
    }

    //
    //  helper functions
    //

    private static UnifiedInputStreamX makeUnifiedStream(byte[] bytes,
                                                         int offset,
                                                         int length)
        throws IOException
    {
        UnifiedInputStreamX uis;
        if (IonStreamUtils.isGzip(bytes, offset, length))
        {
            ByteArrayInputStream baos =
                new ByteArrayInputStream(bytes, offset, length);
            GZIPInputStream gzip = new GZIPInputStream(baos);
            uis = UnifiedInputStreamX.makeStream(gzip);
        }
        else
        {
            uis = UnifiedInputStreamX.makeStream(bytes, offset, length);
        }
        return uis;
    }


    private static UnifiedInputStreamX makeUnifiedStream(InputStream in)
        throws IOException
    {
        in.getClass(); // Force NPE

        // TODO avoid multiple wrapping streams, use the UIS for the pushback
        in = new GzipOrRawInputStream(in);
        UnifiedInputStreamX uis = UnifiedInputStreamX.makeStream(in);
        return uis;
    }


    private static final boolean has_binary_cookie(UnifiedInputStreamX uis)
        throws IOException
    {
        byte[] bytes = new byte[BINARY_VERSION_MARKER_SIZE];

        // try to read the first 4 bytes and unread them (we want
        // the data stream undisturbed by our peeking ahead)
        int len;
        for (len = 0; len < BINARY_VERSION_MARKER_SIZE; len++) {
            int c = uis.read();
            if (c == UnifiedInputStreamX.EOF) {
                break;
            }
            bytes[len] = (byte)c;
        }
        for (int ii=len; ii>0; ) {
            ii--;
            uis.unread(bytes[ii] & 0xff);
        }
        boolean is_cookie = isIonBinary(bytes, 0, len);
        return is_cookie;
    }
}