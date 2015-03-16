package com.romanpulov.wwire;

public final class GLES20ShaderCode {
	public static String LINE_VERTEX_SHADER_CODE =
			"uniform mat4 u_MVPMatrix;      					\n"
		+	"attribute vec3 aPosition;							\n"
		+	"void main() {										\n"
		+	" gl_Position = u_MVPMatrix*vec4(aPosition, 1.0);	\n"
		+	"}													\n";

	public static String LINE_FRAGMENT_SHADER_CODE = 
			"#ifdef GL_FRAGMENT_PRECISION_HIGH		\n"
		+	"precision highp float;					\n"
		+	"#else									\n"
		+	"precision mediump float;				\n"
		+	"#endif									\n"			
		+	"uniform vec3 uColor;                   \n"
		+	"void main() {							\n"
		+	" gl_FragColor = vec4(uColor, 1.0);		\n"
		+	"}										\n";

	public static String SURFACE_VERTEX_SHADER_CODE =
				"uniform mat4 u_MVPMatrix;      					\n"
			+	"uniform mat4 u_ModelMatrix;							\n"
			+	"uniform mat4 u_NormalMatrix;							\n"
			+	"attribute vec3 aPosition;							\n"
			+	"attribute vec3 aNormal;							\n"
			+	"varying vec3 vPosition;							\n"
			+	"varying vec3 vNormal;								\n"
			+   "varying vec4 vColor;                               \n"
			+   "varying vec3 vCamera;                              \n"
			+	"void main() {										\n"
			+	" vPosition = vec3(u_ModelMatrix * vec4(aPosition, 1.0));							\n"
//			+	" vPosition = aPosition;							\n"
			+	" vec3 nNormal = normalize(aNormal);				\n"
//			+	" vNormal = nNormal;				\n"
			+	" vNormal = normalize(vec3(u_NormalMatrix * vec4(nNormal, 1.0)));								\n"
			+	" vCamera = vec3(100.0, 100.0, 100.0);	   			\n"
			+	" gl_Position = u_MVPMatrix * vec4(aPosition, 1.0);	\n"
			+	"}													\n";

			public static String SURFACE_FRAGMENT_SHADER_CODE = 
			"#ifdef GL_FRAGMENT_PRECISION_HIGH			\n"
			+	"precision highp float;					\n"
			+	"#else									\n"
			+	"precision mediump float;				\n"
			+	"#endif									\n"			
			+	"uniform vec3 uColor;                   \n"
			+	"varying vec3 vPosition;				\n"
			+	"varying vec3 vNormal;					\n"			
			+	"varying vec4 vColor;					\n"
			+   "varying vec3 vCamera;                              \n"
			+	"void main() {							\n"
			+   "  vec3 nNormal=normalize(vNormal);		\n"
			+   "  vec3 nCamera = vCamera;			 		\n"
			+	"  vec3 lightvector = normalize(nCamera - vPosition); 	\n"
			+ 	"  vec3 lookvector = normalize(nCamera - vPosition);  	\n"
            +	"  float ambient=0.4;									\n"
            +	"  float k_diffuse=0.6;									\n"
            +	"  float k_specular=0.0;								\n"
            +	"  float diffuse = k_diffuse * max(dot(nNormal, lightvector), 0.0);				\n"
            +	"  vec3 reflectvector = reflect(-lightvector, nNormal);							\n"
            +	"  float specular=k_specular*pow( max(dot(lookvector,reflectvector),0.0),40.0);	\n"
            +	"  vec4 lightColor=(ambient+diffuse+specular)*vec4(uColor, 1.0);				\n" 
			+	"  gl_FragColor = lightColor;													\n" 
			//+	"  gl_FragColor = vColor;												    	\n"
			+	"}																				\n";
			
}

