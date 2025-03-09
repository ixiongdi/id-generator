package icu.congee.id.generator.cuid;

/**
 * Exception indicates that the generation of a new CUID has failed.
 *
 * @since 2.0.0
 */
public class CUIDGenerationException extends RuntimeException {

    /**
     * Creates a new instance.
     *
     * @param cause Cause of the exception
     * @since 2.0.0
     */
    public CUIDGenerationException(final Throwable cause) {

        super("CUID generation failure", cause);
    }

    /**
     * Creates a new instance.
     *
     * @param cause Cause of the exception
     * @since 2.0.1
     */
    public CUIDGenerationException(final String cause) {

        super("CUID generation failure: " + cause);
    }
}