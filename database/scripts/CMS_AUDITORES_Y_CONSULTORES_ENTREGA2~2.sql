--=================================================================================================================
-- 1. ELIMINAR EL TRIGGER QUE IMPIDE LA LIMPIEZA
--=================================================================================================================
-- Este trigger impide la eliminación directa, lo que interfiere con el procedimiento de limpieza.
-- En un entorno controlado, el procedimiento de limpieza es el método adecuado para borrar datos.
DROP TRIGGER TRG_DETALLE_COMISIONES_PROT;

--=================================================================================================================
-- 2. RECREAR OBJETOS PL/SQL ACTUALIZADOS (sin el trigger que causaba conflicto)
--=================================================================================================================

-- Procedimiento principal: Procesa comisiones para un mes/año, generando resúmenes.
CREATE OR REPLACE PROCEDURE CALCULAR_COMISIONES_MES (
    p_mes_proceso IN NUMBER,
    p_anno_proceso IN NUMBER
) IS
    v_total_comision_empresa NUMBER := 0;
    v_detalle_count NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== INICIANDO PROCESAMIENTO DE COMISIONES ===');
    DBMS_OUTPUT.PUT_LINE('Mes: ' || p_mes_proceso || ', Año: ' || p_anno_proceso);

    -- Validación de parámetros
    IF p_mes_proceso < 1 OR p_mes_proceso > 12 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Mes inválido. Debe estar entre 1 y 12.');
    END IF;

    IF p_anno_proceso < 2000 OR p_anno_proceso > 9999 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Año inválido. Debe estar entre 2000 y 9999.'); -- Ingresamos 9999 porque representa el valor numérico más alto posible para un número de 4 dígitos.
    END IF;

    -- Limpiar datos anteriores para el mes/año en la tabla RESUMEN
    DELETE FROM RESUMEN_COMISIONES_AUDIT_MES
    WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso;

    -- Contar detalles para información
    SELECT COUNT(*) INTO v_detalle_count
    FROM DETALLE_COMISIONES_AUDITORIAS_MES
    WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso;

    DBMS_OUTPUT.PUT_LINE('Registros de detalle encontrados para el mes/año: ' || v_detalle_count);

    -- Acumular total para la empresa desde la tabla de detalles
    SELECT NVL(SUM(TOTAL_COMISION_AUDIT), 0) INTO v_total_comision_empresa
    FROM DETALLE_COMISIONES_AUDITORIAS_MES
    WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso;

    -- Generar resumen por profesión
    FOR rec_prof IN (
        SELECT NOMBRE_PROFESION,
               COUNT(*) as total_auditores,
               SUM(TOTAL_COMISION_AUDIT) as total_comision_prof
        FROM DETALLE_COMISIONES_AUDITORIAS_MES
        WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso
        GROUP BY NOMBRE_PROFESION
    ) LOOP
        INSERT INTO RESUMEN_COMISIONES_AUDIT_MES (
            MES_PROCESO, ANNO_PROCESO, NOMBRE_PROFESION,
            TOTAL_AUDITORES, MONTO_TOTAL_COMISIONES, EVALUACION_STATUS
        )
        VALUES (
            p_mes_proceso, p_anno_proceso, rec_prof.NOMBRE_PROFESION,
            rec_prof.total_auditores, rec_prof.total_comision_prof, 'OK'
        );
        DBMS_OUTPUT.PUT_LINE('Resumen generado para Profesión: ' || rec_prof.NOMBRE_PROFESION ||
                             ' - Auditores: ' || rec_prof.total_auditores ||
                             ' - Total Comisión: ' || rec_prof.total_comision_prof);
    END LOOP;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('=== PROCESAMIENTO FINALIZADO ===');
    DBMS_OUTPUT.PUT_LINE('Total Comisiones Empresa (estimado): ' || v_total_comision_empresa);

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en CALCULAR_COMISIONES_MES: ' || SQLERRM);
        ROLLBACK;
        RAISE;
END CALCULAR_COMISIONES_MES;
/

-- Procedimiento de limpieza: Borra registros de detalle y resumen para un mes/año específico.
CREATE OR REPLACE PROCEDURE LIMPIAR_DATOS_COMISIONES_MES (
    p_mes_proceso IN NUMBER,
    p_anno_proceso IN NUMBER
) IS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Limpiando datos de comisiones para ' || p_mes_proceso || '/' || p_anno_proceso);
    -- Borra resumen primero
    DELETE FROM RESUMEN_COMISIONES_AUDIT_MES
    WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso;

    -- Borra detalle
    DELETE FROM DETALLE_COMISIONES_AUDITORIAS_MES
    WHERE MES_PROCESO = p_mes_proceso AND ANNO_PROCESO = p_anno_proceso;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Limpieza finalizada para ' || p_mes_proceso || '/' || p_anno_proceso);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en LIMPIAR_DATOS_COMISIONES_MES: ' || SQLERRM);
        ROLLBACK;
        RAISE;
END LIMPIAR_DATOS_COMISIONES_MES;
/

-- Función: Obtiene la comisión total de un auditor específico para un mes/año.
CREATE OR REPLACE FUNCTION OBTENER_TOTAL_COMISION_AUDITOR (
    p_run_auditor IN VARCHAR2,
    p_mes_proceso IN NUMBER,
    p_anno_proceso IN NUMBER
) RETURN NUMBER IS
    v_total_comision NUMBER;
BEGIN
    SELECT NVL(SUM(TOTAL_COMISION_AUDIT), 0)
    INTO v_total_comision
    FROM DETALLE_COMISIONES_AUDITORIAS_MES
    WHERE RUN_AUDITOR = p_run_auditor
      AND MES_PROCESO = p_mes_proceso
      AND ANNO_PROCESO = p_anno_proceso;

    RETURN v_total_comision;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en OBTENER_TOTAL_COMISION_AUDITOR: ' || SQLERRM);
        RETURN NULL;
END OBTENER_TOTAL_COMISION_AUDITOR;
/

-- Función: Obtiene el monto total de comisiones pagadas en un mes/año.
CREATE OR REPLACE FUNCTION OBTENER_MONTO_TOTAL_COMISIONES_MES (
    p_mes_proceso IN NUMBER,
    p_anno_proceso IN NUMBER
) RETURN NUMBER IS
    v_monto_total NUMBER;
BEGIN
    SELECT NVL(SUM(TOTAL_COMISION_AUDIT), 0)
    INTO v_monto_total
    FROM DETALLE_COMISIONES_AUDITORIAS_MES
    WHERE MES_PROCESO = p_mes_proceso
      AND ANNO_PROCESO = p_anno_proceso;

    RETURN v_monto_total;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en OBTENER_MONTO_TOTAL_COMISIONES_MES: ' || SQLERRM);
        RETURN NULL;
END OBTENER_MONTO_TOTAL_COMISIONES_MES;
/

-- Package: Agrupa los procedimientos y funciones anteriores.
CREATE OR REPLACE PACKAGE PKG_COMISIONES AS
    PROCEDURE PROCESAR_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER);
    PROCEDURE LIMPIAR_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER);
    FUNCTION GET_TOTAL_COMISION_AUDITOR (p_run IN VARCHAR2, p_mes IN NUMBER, p_anno IN NUMBER) RETURN NUMBER;
    FUNCTION GET_MONTO_TOTAL_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER) RETURN NUMBER;
END PKG_COMISIONES;
/

CREATE OR REPLACE PACKAGE BODY PKG_COMISIONES AS
    PROCEDURE PROCESAR_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER) IS
    BEGIN
        CALCULAR_COMISIONES_MES(p_mes, p_anno);
    END PROCESAR_COMISIONES_MES;

    PROCEDURE LIMPIAR_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER) IS
    BEGIN
        LIMPIAR_DATOS_COMISIONES_MES(p_mes, p_anno);
    END LIMPIAR_COMISIONES_MES;

    FUNCTION GET_TOTAL_COMISION_AUDITOR (p_run IN VARCHAR2, p_mes IN NUMBER, p_anno IN NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN OBTENER_TOTAL_COMISION_AUDITOR(p_run, p_mes, p_anno);
    END GET_TOTAL_COMISION_AUDITOR;

    FUNCTION GET_MONTO_TOTAL_COMISIONES_MES (p_mes IN NUMBER, p_anno IN NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN OBTENER_MONTO_TOTAL_COMISIONES_MES(p_mes, p_anno);
    END GET_MONTO_TOTAL_COMISIONES_MES;

END PKG_COMISIONES;
/

-- Trigger: Registra operaciones de inserción/actualización en la tabla AUDITORIA.
CREATE OR REPLACE TRIGGER TRG_AUDITORIA_LOG
    AFTER INSERT OR UPDATE ON AUDITORIA
    FOR EACH ROW
DECLARE
    v_usuario VARCHAR2(30) := USER;
    v_fecha DATE := SYSDATE;
    v_accion VARCHAR2(10);
BEGIN
    IF INSERTING THEN
        v_accion := 'INSERT';
    ELSIF UPDATING THEN
        v_accion := 'UPDATE';
    END IF;

    DBMS_OUTPUT.PUT_LINE('Trigger: ' || v_accion || ' en AUDITORIA ID ' || :NEW.ID_AUDITOR || ' por ' || v_usuario || ' el ' || v_fecha);

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en TRG_AUDITORIA_LOG: ' || SQLERRM);
END TRG_AUDITORIA_LOG;
/

--=================================================================================================================
-- 3. EJECUCIÓN DE PROCEDIMIENTOS Y FUNCIONES PARA VER RESULTADOS
--=================================================================================================================

SET SERVEROUTPUT ON;

-- Ejecutar el procedimiento principal
BEGIN
    CALCULAR_COMISIONES_MES(9, 2025); -- Ejemplo: Procesa Septiembre 2025
END;
/

-- Ejecutar el procedimiento de limpieza (ahora debería funcionar)
BEGIN
    LIMPIAR_DATOS_COMISIONES_MES(9, 2025); -- Ejemplo: Limpia Septiembre 2025
    DBMS_OUTPUT.PUT_LINE('Datos de Septiembre 2025 limpiados correctamente.');
END;
/

-- Ejecutar funciones y mostrar resultados
DECLARE
    v_resultado NUMBER;
BEGIN
    -- Obtener comisión total de un auditor específico para un mes/año
    v_resultado := OBTENER_TOTAL_COMISION_AUDITOR('11111111-1', 9, 2025); -- Ejemplo
    DBMS_OUTPUT.PUT_LINE('Comisión total auditor 11111111-1 (Sep 2025): ' || NVL(v_resultado, 0));

    -- Obtener monto total de comisiones del mes
    v_resultado := OBTENER_MONTO_TOTAL_COMISIONES_MES(9, 2025); -- Ejemplo
    DBMS_OUTPUT.PUT_LINE('Monto total comisiones (Sep 2025): ' || NVL(v_resultado, 0));
END;
/

-- Ejecutar procedimientos del package
BEGIN
    PKG_COMISIONES.PROCESAR_COMISIONES_MES(10, 2025); -- Ejemplo: Procesa Octubre 2025
END;
/

BEGIN
    PKG_COMISIONES.LIMPIAR_COMISIONES_MES(10, 2025); -- Ejemplo: Limpia Octubre 2025
    DBMS_OUTPUT.PUT_LINE('Limpieza Octubre 2025 vía Package realizada correctamente.');
END;
/

DECLARE
    v_resultado NUMBER;
BEGIN
    -- Usar funciones del package
    v_resultado := PKG_COMISIONES.GET_TOTAL_COMISION_AUDITOR('22222222-2', 10, 2025); -- Ejemplo
    DBMS_OUTPUT.PUT_LINE('Comisión vía Package (22222222-2, Oct 2025): ' || NVL(v_resultado, 0));

    v_resultado := PKG_COMISIONES.GET_MONTO_TOTAL_COMISIONES_MES(10, 2025); -- Ejemplo
    DBMS_OUTPUT.PUT_LINE('Monto total vía Package (Oct 2025): ' || NVL(v_resultado, 0));
END;
/

--=================================================================================================================
-- Fin del Script
--=================================================================================================================


BEGIN
  CALCULAR_COMISIONES_MES(9, 2025);
END;
/


SELECT username, default_tablespace FROM dba_users WHERE username='CONSULTORES';
SELECT tablespace_name, bytes/1024/1024 MB
FROM dba_ts_quotas
WHERE username='CONSULTORES';
