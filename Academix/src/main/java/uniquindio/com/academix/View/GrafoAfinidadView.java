package uniquindio.com.academix.View;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.ListaSimple;

public class GrafoAfinidadView {
    public static void mostrarGrafo(ListaSimple<Estudiante> estudiantes, Academix academix) {
        Stage stage = new Stage();
        Pane pane = new Pane();
        pane.setPrefSize(800, 600);

        int n = estudiantes.size();
        double radio = 220;
        double centerX = 400;
        double centerY = 300;
        // Reemplazo de Map<String, Circle> y Map<String, Double[]>
        ListaSimple<String> usuarios = new ListaSimple<>();
        ListaSimple<Circle> nodos = new ListaSimple<>();
        ListaSimple<Double[]> posiciones = new ListaSimple<>();

        // Posicionar nodos en círculo
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radio * Math.cos(angle);
            double y = centerY + radio * Math.sin(angle);
            Estudiante est = estudiantes.get(i);
            Circle circle = new Circle(x, y, 30, Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
            pane.getChildren().add(circle);
            Label label = new Label(est.getUsuario());
            label.setLayoutX(x - 25);
            label.setLayoutY(y - 10);
            pane.getChildren().add(label);
            usuarios.agregar(est.getUsuario());
            nodos.agregar(circle);
            posiciones.agregar(new Double[]{x, y});
        }

        // Dibujar solo aristas de amistad real
        for (int i = 0; i < n; i++) {
            Estudiante e1 = estudiantes.get(i);
            for (int j = i + 1; j < n; j++) {
                Estudiante e2 = estudiantes.get(j);
                if (e1.getAmigos() != null && e1.getAmigos().contiene(e2.getUsuario())) {
                    // Buscar posiciones por usuario
                    int idx1 = -1, idx2 = -1;
                    for (int k = 0; k < usuarios.tamano(); k++) {
                        if (usuarios.get(k).equals(e1.getUsuario())) idx1 = k;
                        if (usuarios.get(k).equals(e2.getUsuario())) idx2 = k;
                    }
                    if (idx1 != -1 && idx2 != -1) {
                        Double[] pos1 = posiciones.get(idx1);
                        Double[] pos2 = posiciones.get(idx2);
                        Line line = new Line(pos1[0], pos1[1], pos2[0], pos2[1]);
                        line.setStrokeWidth(2);
                        line.setStroke(Color.GREEN);
                        pane.getChildren().add(0, line);
                    }
                }
            }
        }

        stage.setTitle("Grafo de Afinidad entre Estudiantes");
        stage.setScene(new Scene(pane));
        stage.show();
    }
}
