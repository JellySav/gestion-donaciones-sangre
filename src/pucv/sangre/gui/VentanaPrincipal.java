package pucv.sangre.gui;

// Importar clases del proyecto
import pucv.sangre.gestion.GestionCentroSangre;
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ventana principal de la interfaz gráfica (SIA-10 / Swing).
 */
public class VentanaPrincipal extends JFrame {
    private final GestionCentroSangre centro;

    private JTable tablaCampanas;
    private JTable tablaDonantes;
    private DefaultTableModel modelCampanas;
    private DefaultTableModel modelDonantes;

    public VentanaPrincipal(GestionCentroSangre centro) {
        this.centro = centro;
        initUI();
        actualizarTablaCampanas();
    }

    private void initUI() {
        setTitle("Sistema Centro de Sangre - PUCV");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior -> Botones de Acción
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregarCampana = new JButton("Nueva Campaña");
        JButton btnAgregarDonante = new JButton("Registrar Donante");
        JButton btnCompatibilidad = new JButton("Consultar Compatibilidad");

        panelSuperior.add(btnAgregarCampana);
        panelSuperior.add(btnAgregarDonante);
        panelSuperior.add(btnCompatibilidad);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central: Tablas en SplitPane
        modelCampanas = new DefaultTableModel(new String[]{"Código", "Nombre", "Lugar", "Inicio", "Fin"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deshabilitar edición directa en celda
            }
        };
        tablaCampanas = new JTable(modelCampanas);

        modelDonantes = new DefaultTableModel(new String[]{"RUT", "Nombre", "Grupo", "RH", "Edad", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDonantes = new JTable(modelDonantes);

        // Al seleccionar una campaña, filtra sus donantes automáticamente
        tablaCampanas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarTablaDonantes();
            }
        });

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaCampanas),
                new JScrollPane(tablaDonantes)
        );
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Listeners de Eventos
        btnAgregarCampana.addActionListener(e -> dialogAgregarCampana());
        btnAgregarDonante.addActionListener(e -> dialogAgregarDonante());
        btnCompatibilidad.addActionListener(e -> dialogConsultarCompatibilidad());
    }

    private void actualizarTablaCampanas() {
        modelCampanas.setRowCount(0);
        for (Campana c : centro.getCampanas().values()) {
            modelCampanas.addRow(new Object[]{
                    c.getCodigo(),
                    c.getNombre(),
                    c.getLugar(),
                    c.getFechaInicio() != null ? c.getFechaInicio().toString() : "N/A",
                    c.getFechaFin() != null ? c.getFechaFin().toString() : "N/A"
            });
        }
        actualizarTablaDonantes();
    }

    private void actualizarTablaDonantes() {
        modelDonantes.setRowCount(0);
        int row = tablaCampanas.getSelectedRow();
        if (row != -1) {
            String codCampana = (String) modelCampanas.getValueAt(row, 0);
            try {
                Campana cmp = centro.buscarCampana(codCampana);
                for (Donante d : cmp.getDonantes().values()) {
                    modelDonantes.addRow(new Object[]{
                            d.getRut(),
                            d.getNombre(),
                            d.getGrupoSanguineo(),
                            d.getFactorRh(),
                            d.getEdad(),
                            d.getTelefono()
                    });
                }
            } catch (Exception ignored) {}
        }
    }

    private void dialogAgregarCampana() {
        JTextField txtCod = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtLug = new JTextField();
        JTextField txtInicio = new JTextField(LocalDate.now().toString());
        JTextField txtFin = new JTextField(LocalDate.now().plusMonths(1).toString());

        Object[] message = {
                "Código:", txtCod,
                "Nombre:", txtNom,
                "Lugar:", txtLug,
                "Fecha Inicio (YYYY-MM-DD):", txtInicio,
                "Fecha Fin (YYYY-MM-DD):", txtFin
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Campaña", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                LocalDate fInicio = LocalDate.parse(txtInicio.getText().trim());
                LocalDate fFin = LocalDate.parse(txtFin.getText().trim());

                Campana nueva = new Campana(
                        txtCod.getText().trim(),
                        txtNom.getText().trim(),
                        txtLug.getText().trim(),
                        fInicio,
                        fFin
                );

                centro.agregarCampana(nueva);
                actualizarTablaCampanas();
                JOptionPane.showMessageDialog(this, "Campaña agregada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Debe ser YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogAgregarDonante() {
        int row = tablaCampanas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una campaña en la tabla superior.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codCampana = (String) modelCampanas.getValueAt(row, 0);

        JTextField txtRut = new JTextField();
        JTextField txtNom = new JTextField();
        JComboBox<String> cbGrupo = new JComboBox<>(new String[]{"A", "B", "AB", "O"});
        JComboBox<String> cbRh = new JComboBox<>(new String[]{"+", "-"});
        JTextField txtEdad = new JTextField();
        JTextField txtTel = new JTextField();

        Object[] message = {
                "Campaña Seleccionada: " + codCampana,
                "RUT:", txtRut,
                "Nombre Completo:", txtNom,
                "Grupo Sanguíneo:", cbGrupo,
                "Factor RH:", cbRh,
                "Edad:", txtEdad,
                "Teléfono:", txtTel
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Registrar Donante", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int edad = Integer.parseInt(txtEdad.getText().trim());
                Campana cmp = centro.buscarCampana(codCampana);

                Donante d = new Donante(
                        txtRut.getText().trim(),
                        txtNom.getText().trim(),
                        (String) cbGrupo.getSelectedItem(),
                        (String) cbRh.getSelectedItem(),
                        edad,
                        txtTel.getText().trim()
                );

                cmp.agregarDonante(d);
                actualizarTablaDonantes();
                JOptionPane.showMessageDialog(this, "Donante registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogConsultarCompatibilidad() {
        JComboBox<String> cbGrupo = new JComboBox<>(new String[]{"A", "B", "AB", "O"});
        JComboBox<String> cbRh = new JComboBox<>(new String[]{"+", "-"});

        Object[] message = {
                "Grupo Receptor:", cbGrupo,
                "Factor RH Receptor:", cbRh
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Consultar Compatibilidad (SIA-9)", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String grupo = (String) cbGrupo.getSelectedItem();
            String rh = (String) cbRh.getSelectedItem();

            List<Donante> compatibles = centro.obtenerDonantesCompatibles(grupo, rh);

            StringBuilder sb = new StringBuilder("Donantes compatibles para receptor (" + grupo + rh + "):\n\n");
            if (compatibles.isEmpty()) {
                sb.append("No hay donantes compatibles registrados en el sistema.");
            } else {
                compatibles.forEach(d -> sb.append("• ").append(d.toString()).append("\n"));
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 200));

            JOptionPane.showMessageDialog(this, scrollPane, "Resultado de Compatibilidad", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}