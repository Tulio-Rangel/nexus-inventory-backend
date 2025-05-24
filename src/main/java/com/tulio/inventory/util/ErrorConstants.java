package com.tulio.inventory.util;

public class ErrorConstants {
    // Errores de usurio
    public static final String USUARIO_NO_ENCONTRADO_ID = "Usuario no encontrado con ID: ";
    public static final String NOMBRE_USUARIO_NO_PUEDER_ESTAR_VACIO = "El nombre del usuario no puede estar vacío.";
    public static final String EDAD_USUARIO_DEBE_SER_POSITIVA = "La edad del usuario debe ser un número positivo.";
    public static final String CARGO_USUARIO_NO_PUEDER_ESTAR_VACIO = "El cargo del usuario no puede estar vacío.";
    public static final String USUARIO_EXISTE_NOMBRE = "Ya existe un usuario con el nombre: ";

    // Errores de producto
    public static final String ESPECIFICAR_USUARIO = "Debe especificar el usuario que realiza el registro.";
    public static final String PRODUCTO_EXISTE_NOMBRE = "Ya existe un producto con el nombre: ";
    public static final String PRODUCTO_NO_ENCONTRADO_ID = "Producto no encontrado con ID: ";
    public static final String ESPECIFICAR_USUARIO_MODIFICACION = "Debe especificar el usuario que realiza la modificación.";
    public static final String USUARIO_CREADOR_DEBE_ELIMINAR = "Solo el usuario que registró la mercancía puede eliminarla.";
    public static final String FILTRO_BUSQUEDA_VACIO = "Debe proporcionar al menos un filtro de búsqueda (fecha, usuario o nombre de producto).";
    public static final String NOMBRE_PRODUCTO_NO_PUEDE_SER_VACIO = "El nombre del producto no puede estar vacío.";
    public static final String CANTIDAD_PRODUCTO_DEBE_SER_POSITIVA = "La cantidad del producto debe ser un número entero positivo.";

    // Errores compartidos
    public static final String FECHA_INGRESO_NO_PUEDE_SER_FUTURA = "La fecha de ingreso no puede ser futura.";
}
