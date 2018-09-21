#ifndef DLScanner_h
#define DLScanner_h

#define DL_SCANNER_OBJECT_FIND_TIME 1000
#define DL_SCANNER_OBJECT_LOSE_TIME 500
#define DL_SCANNER_RESIZE_COLS 480

/**
 * @typedef DLScannerRef
 * @since 0.1.0
 */
typedef struct OpaqueDLScanner* DLScannerRef;

/**
 * @typedef DLDetectedObjectRef
 * @since 0.1.0
 */
typedef struct OpaqueDLDetectedObject* DLDetectedObjectRef;

/**
 * The callback executed when a object is found.
 * @typedef DLScannerSpotObjectCallback
 * @since 0.1.0
 */
typedef void (*DLScannerFindObjectCallback)(DLScannerRef scanner, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);

/**
 * The callback executed when a object is spotted.
 * @typedef DLScannerSpotObjectCallback
 * @since 0.1.0
 */
typedef void (*DLScannerSpotObjectCallback)(DLScannerRef scanner, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);

/**
 * The callback executed when a object is lost.
 * @typedef DLScannerLoseObjectCallback
 * @since 0.1.0
 */
typedef void (*DLScannerLoseObjectCallback)(DLScannerRef scanner);

/**
 * The callback executed when a nothing was found.
 * @typedef DLScannerMissObjectCallback
 * @since 0.1.0
 */
typedef void (*DLScannerMissObjectCallback)(DLScannerRef scanner);

#if __cplusplus
extern "C" {
#endif

/**
 * Creates a new scanner.
 * @function DLScannerCreate
 * @since 0.1.0
 */
DLScannerRef DLScannerCreate();

/**
 * Creates a new scanner.
 * @function DLScannerDelete
 * @since 0.1.0
 */
void DLScannerDelete(DLScannerRef scanner);

/**
 * Enabled or disable the scanner.
 * @function DLScannerSetEnabled
 * @since 0.1.0
 */
void DLScannerSetEnabled(DLScannerRef scanner, bool enabled);

/**
 * Enable or disable the debug mode.
 * @function DLScannerSetDebugging
 * @since 0.1.0
 */
void DLScannerSetDebug(DLScannerRef scanner, bool debugging);

/**
 * Sets the find object callback.
 * @function DLScannerSetFindObjectCallback
 * @since 0.1.0
 */
void DLScannerSetFindObjectCallback(DLScannerRef scanner, DLScannerFindObjectCallback callback);

/**
 * Sets the spot object callback.
 * @function DLScannerSetSpotObjectCallback
 * @since 0.1.0
 */
void DLScannerSetSpotObjectCallback(DLScannerRef scanner, DLScannerSpotObjectCallback callback);

/**
 * Sets the spot object callback.
 * @function DLScannerSetLoseObjectCallback
 * @since 0.1.0
 */
void DLScannerSetLoseObjectCallback(DLScannerRef scanner, DLScannerLoseObjectCallback callback);

/**
 * Sets the spot object callback.
 * @function DLScannerSetMissObjectCallback
 * @since 0.1.0
 */
void DLScannerSetMissObjectCallback(DLScannerRef scanner, DLScannerMissObjectCallback callback);

/**
 * Assigns the scanner user data.
 * @function DLScannerSetData
 * @since 0.1.0
 */
void DLScannerSetData(DLScannerRef scanner, void* data);

/**
 * Retrieves the scanner user data.
 * @function DLScannerSetData
 * @since 0.1.0
 */
void* DLScannerGetData(DLScannerRef scanner);

/**
 * Resets the detection session.
 * @function DLScannerReset
 * @since 0.1.0
 */
void DLScannerReset(DLScannerRef scanner);

/**
 * Resets the detection state.
 * @function DLScannerRestart
 * @since 0.1.0
 */
void DLScannerRestart(DLScannerRef scanner);

#if __cplusplus
}
#endif
#endif
