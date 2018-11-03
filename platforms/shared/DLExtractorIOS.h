#ifndef DLExtractorIOS_h
#define DLExtractorIOS_h

#if __cplusplus
extern "C" {
#endif

/**
 * @function DLExtractorPullImage
 * @since 0.1.0
 * @hidden
 */
CGImageRef DLExtractorPullImage(CGImageRef image, int imgc, int imgr, CGPoint p1, CGPoint p2, CGPoint p3, CGPoint p4);

#if __cplusplus
}
#endif
#endif
