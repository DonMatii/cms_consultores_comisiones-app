# 🏢 Sistema de Gestión de Comisiones - CMS Consultores

## 📖 Descripción
Sistema automatizado para el cálculo y gestión de comisiones de auditores y consultores, desarrollado en **Java Swing + Oracle PL/SQL** como parte de la Evaluación Parcial 2.

## 🏗️ Arquitectura
- **Frontend:** Java Swing con patrón MVC
- **Backend:** Oracle PL/SQL con procedimientos almacenados
- **Base de Datos:** Oracle Cloud Autonomous Database
- **Conexión:** JDBC Oracle con Wallet SSL

## 🎯 Funcionalidades
- ✅ Cálculo masivo de comisiones por mes/año
- ✅ Resumen ejecutivo por tipo de profesión
- ✅ Consulta individual por RUN de auditor
- ✅ Ejecución de procedimientos PL/SQL
- ✅ Interfaz gráfica profesional

## 🛠️ Tecnologías
- **Java 21** + Swing
- **Oracle Database 19c** + PL/SQL
- **JDBC Oracle Driver**
- **Arquitectura MVC**

## 📁 Estructura del Proyecto
src/
├── Main.java # Clase principal
├── BD/ # Capa de datos
│ ├── DatabaseConnection.java
│ └── StoredProcedures.java
├── Model/ # Entidades
│ └── ResumenComision.java
├── Controller/ # Lógica de negocio
│ └── ComisionesController.java
└── View/ # Interfaz gráfica
└── MainFrame.java


## 🚀 Instalación y Ejecución
1. Clonar repositorio: `git clone https://github.com/DonMatii/cms_consultores_comisiones-app.git`
2. Abrir en NetBeans como proyecto existente
3. Configurar conexión a BD en `BD/DatabaseConnection.java`
4. Ejecutar `Main.java`

## 👥 Integrantes del Proyecto

### **Matías Suazo**
*Desarrollador Principal*
- Arquitectura MVC y diseño de base de datos
- Implementación de procedimientos PL/SQL
- Desarrollo de interfaz Java Swing
- Integración con Oracle Cloud

### **Álvaro Chávez**  
*Analista de Negocio*
- Definición de reglas de negocio
- Especificación de requerimientos
- Planificación y documentación funcional

### **Catherine Godoy**
*Especialista en Calidad*
- Diseño de casos de prueba
- Validación de funcionalidades
- Control de calidad del software

## 📄 Licencia
Proyecto académico - Evaluación Parcial 2
