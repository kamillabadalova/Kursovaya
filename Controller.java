package Kursovaya;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    XYChart.Series<Double,Double> series1 = new XYChart.Series<>();
    XYChart.Series<Double,Double> series2 = new XYChart.Series<>();
    XYChart.Series<Double,Double> series3 = new XYChart.Series<>();
    XYChart.Series<Double,Double> series4 = new XYChart.Series<>();
    ArrayList <Double> po_x = new ArrayList<>();
    ArrayList <Double> po_y = new ArrayList<>();
    static final String table = "data";
    String select = " SELECT * FROM " + table;
    ResultSet rs;
    @FXML
    TextField userlogin;
    public static final String usertable = "usersave";
    public static final String savelogin = "login";
    public static final String saveaction = "action";
    public static final String savelocaldatetime= "localdatetime";
    String insert = " INSERT INTO " + usertable + " (" + savelogin + ","
         + saveaction+ "," + savelocaldatetime + ") " + " VALUES(?,?,?) ";
    @FXML
    Button tablebtn;
    @FXML
    TableView <Table> tableView;
    ObservableList <Table> list = FXCollections.observableArrayList();
    @FXML
    TableColumn<Table, Integer> col_year;
    @FXML
    TableColumn<Table, Integer> col_num;
    @FXML
    LineChart <Number,Number> lineChart;
    @FXML
    NumberAxis xAxis;
    @FXML
    NumberAxis yAxis;
    @FXML
    Button downFile;
    @FXML
    TextArea textArea;
    @FXML
    TextArea textArea1;
    @FXML
    ColorPicker colorpicker;
    

    public void DownFile(ActionEvent actionEvent) {
        new DataBaseHandler().fileDownload(); }

    public void Click(ActionEvent actionEvent) {
        new DataBaseHandler().addtable();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        tablebtn.setOnAction(e -> {
            try { Connection con = new DataBaseHandler().getDbConnection();
                ResultSet rs = con.createStatement().executeQuery("select * from data ");
                while (rs.next()) {
                    list.add(new Table(rs.getString("Годы"),  rs.getString("Единиц")));
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            col_year.setCellValueFactory(new PropertyValueFactory<>("year"));
            col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
            tableView.setItems(list);
            textArea1.appendText("Выведение данных в таблицу " + " , ");
        });
    }

              @FXML
              public void ClickToGraf(ActionEvent actionEvent) {
                  //  на графике все верно располагается,
                  //  по х - время фиксации значения временного ряда,т.е. года
                  //  по у - значение временного ряда,т.е. количество браков
                  lineChart.getXAxis().setAutoRanging(false);
                  yAxis.setUpperBound(2018);
                  yAxis.setLowerBound(1945);
                  yAxis.setTickUnit(5);
                  yAxis.setLabel("Годы");
                  xAxis.setLabel("Браки");
                  try {
                      Connection con = new DataBaseHandler().getDbConnection();
                      PreparedStatement pr = con.prepareStatement(select);
                      rs = pr.executeQuery();
                      while (rs.next()) {
                          series.getData().add(new XYChart.Data<>(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2))));
                          po_x.add(Double.parseDouble(rs.getString(1)));
                          po_y.add(Double.parseDouble(rs.getString(2)));
                      }
                  } catch (SQLException ex) {
                      ex.printStackTrace();
                  } catch (ClassNotFoundException ex) {
                      ex.printStackTrace();
                  }
                  lineChart.getData().add(series);
                  series.setName("График данных");
                  textArea.appendText("Построение графика данных " + ", ");

                 Platform.runLater(() -> {
                      Color c = colorpicker.getValue();
                      String color_style = c.toString().substring(2, 8);
                      Node node = lineChart.lookup(".default-color0.chart-series-line");
                      node.setStyle("-fx-stroke: #" + color_style + ";");
                      Node legend = lineChart.lookup(".default-color0.chart-legend-item-symbol");
                      if (legend != null) {
                          legend.setStyle("-fx-background-color: #" + color_style + ", white;");}
                      for (int i = 0; i < series.getData().size(); i++) {
                          XYChart.Data<Number, Number> round = series.getData().get(i);
                          Node roundColor = round.getNode().lookup(".chart-line-symbol");
                          roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
                      }
                  });
              }

    @FXML
     public void ClickToLin(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
         //  на графике все верно располагается,
                  //  по х - время фиксации значения временного ряда,т.е. года
                  //  по у - значение временного ряда,т.е. количество браков
                  lineChart.getXAxis().setAutoRanging(false);
                  yAxis.setUpperBound(2018);
                  yAxis.setLowerBound(1945);
                  yAxis.setTickUnit(5);
                  yAxis.setLabel("Годы");
                  xAxis.setLabel("Браки");
         try {
             Connection con = new DataBaseHandler().getDbConnection();
             PreparedStatement pr = con.prepareStatement(select);
             rs = pr.executeQuery();
             while (rs.next()) {
                 po_x.add(Double.parseDouble(rs.getString(1)));
                 po_y.add(Double.parseDouble(rs.getString(2)));
             }
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         } catch (SQLException e) {
             e.printStackTrace();
         } catch (NumberFormatException e) {
             e.printStackTrace();
         }
        textArea.appendText("Построение линейной регрессии со средним отклонением,равным  " +
                LineReg(po_x,po_y,lineChart) + ", ");
        Platform.runLater(() -> {
            Color c1 = colorpicker.getValue();
            String color_style = c1.toString().substring(2, 8);
            Node node = lineChart.lookup(".default-color0.chart-series-line");
            node.setStyle("-fx-stroke: #" + color_style + ";");
            Node legend = lineChart.lookup(".default-color0.chart-legend-item-symbol");
            if (legend != null) {
                legend.setStyle("-fx-background-color: #" + color_style + ", white;");}
            for (int i = 0; i < series1.getData().size(); i++) {
                XYChart.Data<Double, Double> round = series1.getData().get(i);
                Node roundColor = round.getNode().lookup(".chart-line-symbol");
                roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
            }
        });

     }
     @FXML
    public void ClickToLog(ActionEvent actionEvent) {
        //  на графике все верно располагается,
                  //  по х - время фиксации значения временного ряда,т.е. года
                  //  по у - значение временного ряда,т.е. количество браков
                  lineChart.getXAxis().setAutoRanging(false);
                  yAxis.setUpperBound(2018);
                  yAxis.setLowerBound(1945);
                  yAxis.setTickUnit(5);
                  yAxis.setLabel("Годы");
                  xAxis.setLabel("Браки");
        try {
            Connection con = new DataBaseHandler().getDbConnection();
            PreparedStatement pr = con.prepareStatement(select);
            rs = pr.executeQuery();
            while (rs.next()) {
                po_x.add(Double.parseDouble(rs.getString(1)));
                po_y.add(Double.parseDouble(rs.getString(2)));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
         textArea.appendText("Построение логарифмической регрессии со средним отклонением,равным "
              +  LogReg(po_x,po_y,lineChart)+ ", ");
         Platform.runLater(() -> {
             Color c2 = colorpicker.getValue();
             String color_style = c2.toString().substring(2, 8);
             Node node = lineChart.lookup(".default-color0.chart-series-line");
             node.setStyle("-fx-stroke: #" + color_style1 + ";");
             Node legend = lineChart.lookup(".default-color0.chart-legend-item-symbol");
             if (legend1 != null) {
                 legend.setStyle("-fx-background-color: #" + color_style1 + ", white;");}
             for (int i = 0; i < series2.getData().size(); i++) {
                 XYChart.Data<Double,Double> round1 = series2.getData().get(i);
                 Node roundColor = round.getNode().lookup(".chart-line-symbol");
                 roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
             }
         });
    }
    @FXML
    public void ClickToExp(ActionEvent actionEvent) {
        //  на графике все верно располагается,
                  //  по х - время фиксации значения временного ряда,т.е. года
                  //  по у - значение временного ряда,т.е. количество браков
                  lineChart.getXAxis().setAutoRanging(false);
                  yAxis.setUpperBound(2018);
                  yAxis.setLowerBound(1945);
                  yAxis.setTickUnit(5);
                  yAxis.setLabel("Годы");
                  xAxis.setLabel("Браки");
        try {
            Connection con = new DataBaseHandler().getDbConnection();
            PreparedStatement pr = con.prepareStatement(select);
            rs = pr.executeQuery();
            while (rs.next()) {
                po_x.add(Double.parseDouble(rs.getString(1)));
                po_y.add(Double.parseDouble(rs.getString(2)));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        textArea.appendText("Построение экспоненциальной регрессии со средним отклонением,равным " +
                ExpReg(po_x,po_y,lineChart) + ", ");

        Platform.runLater(() -> {
            Color c3 = colorpicker.getValue();
            String color_style = c3.toString().substring(2, 8);
            Node node = lineChart.lookup(".default-color0.chart-series-line");
            node.setStyle("-fx-stroke: #" + color_style + ";");
            Node legend = lineChart.lookup(".default-color0.chart-legend-item-symbol");
            if (legend != null) {
                legend.setStyle("-fx-background-color: #" + color_style + ", white;");}
            for (int i = 0; i < series3.getData().size(); i++) {
                XYChart.Data<Double, Double> round = series3.getData().get(i);
                Node roundColor = round.getNode().lookup(".chart-line-symbol");
                roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
            }
        });
    }
   @FXML
    public void ClickToQuad(ActionEvent actionEvent) {
        //  на графике все верно располагается,
                  //  по х - время фиксации значения временного ряда,т.е. года
                  //  по у - значение временного ряда,т.е. количество браков
                  lineChart.getXAxis().setAutoRanging(false);
                  yAxis.setUpperBound(2018);
                  yAxis.setLowerBound(1945);
                  yAxis.setTickUnit(5);
                  yAxis.setLabel("Годы");
                  xAxis.setLabel("Браки");
        try {
            Connection con = new DataBaseHandler().getDbConnection();
            PreparedStatement pr = con.prepareStatement(select);
            rs = pr.executeQuery();
            while (rs.next()) {
                po_x.add(Double.parseDouble(rs.getString(1)));
                po_y.add(Double.parseDouble(rs.getString(2)));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        textArea.appendText("Построение квадратичной регрессии со средним отклонением,равным " +
                QuadReg(po_x,po_y,lineChart) + " , ");

       Platform.runLater(() -> {
           Color c4 = colorpicker.getValue();
           String color_style = c4.toString().substring(2, 8);
           Node node = lineChart.lookup(".default-color0.chart-series-line");
           node.setStyle("-fx-stroke: #" + color_style + ";");
           Node legend = lineChart.lookup(".default-color0.chart-legend-item-symbol");
           if (legend != null) {
               legend.setStyle("-fx-background-color: #" + color_style + ", white;");}
           for (int i = 0; i < series4.getData().size(); i++) {
               XYChart.Data<Double, Double> round = series4.getData().get(i);
               Node roundColor = round.getNode().lookup(".chart-line-symbol");
               roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
           }
       });
    } 
    
    @FXML
    public void ClickToSave(ActionEvent actionEvent) {
        PreparedStatement prSt=null;
        try { Connection con = new DataBaseHandler().getDbConnection();
            prSt = con.prepareStatement(insert);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try { prSt.setString(1, userlogin.getText());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { prSt.setString(2,textArea1.getText()+" "+ textArea.getText());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { prSt.setString(3,String.valueOf(LocalDateTime.now()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { prSt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public double LineReg(ArrayList<Double> x, ArrayList<Double> y, LineChart lineChart) {
        double SUMxi=0;
        double SUMyi=0;
        double SUMxx=0;
        double SUMxyi=0;
        double n=x.size();
        double mis=0;
        for(int i=0;i<x.size();i++) {
            SUMxi+=x.get(i);
            SUMyi+=y.get(i);
            SUMxx+=Math.pow(x.get(i),2);
            SUMxyi+=x.get(i)*y.get(i); }
        double a=(SUMxi*SUMyi-SUMxyi*n)/(Math.pow(SUMxi,2)-SUMxx*n);
        double b=(SUMxi*SUMxyi-SUMxx*SUMyi)/(Math.pow(SUMxi,2)-SUMxx*n);
        for (int i=0; i<x.size();i++) {
            mis+= Math.abs((y.get(i)-(a*x.get(i)+b))/y.get(i)); }
        double mis1= mis*100/n;
        System.out.println("Среднее отклонение расчетных значений от фактических (линейная регрессия)=" +mis1);
        XYChart.Series<Double,Double> series1=new XYChart.Series<>();
        series1.getData().add(new XYChart.Data(x.get(0),(a*x.get(0)+b)));
        series1.getData().add(new XYChart.Data((x.get(x.size()-1)),(a*x.get(x.size()-1)+b)));
        lineChart.getData().add(series1);
        series1.setName("Линейная регрессия");
        return mis1;
    }
    public static double LogReg(ArrayList<Double> x, ArrayList<Double> y, LineChart lineChart) {
        double SUMyi=0;
        double SUMlnx=0;
        double SUMlnxx=0;
        double SUMylnx=0;
        double n=x.size();
        double mis=0;
        for(int i=0;i<x.size();i++) {
            SUMyi+=y.get(i);
            SUMlnx+=Math.log(x.get(i));
            SUMlnxx+=Math.pow(Math.log(x.get(i)),2);
            SUMylnx+=y.get(i)*Math.log(x.get(i));
        }
        double b=(n*SUMylnx-SUMlnx*SUMyi)/(n*SUMlnxx-Math.pow(SUMlnx,2));
        double a=SUMyi/n-SUMlnx*(b/n);
        for (int i=0;i<x.size();i++) {
            mis+= Math.abs((y.get(i)-(a+b*Math.log(x.get(i))))/ y.get(i));
        }
        double mis1=mis*100/n;
        System.out.println("Среднее отклонение расчетных значений от фактических (логарифмическая регрессия) =" +mis1);
        XYChart.Series<Double, Double> series2=new XYChart.Series<>();
        for(int i=0;i<x.size();i++) {
            series2.getData().add(new XYChart.Data(x.get(i),b*Math.log(x.get(i)) + a)); }
            lineChart.getData().add(series2);
            series2.setName("Логарифмическая регрессия");
        return mis1;
    }
    public static double ExpReg(ArrayList <Double> x,ArrayList <Double> y,LineChart lineChart) {
        double SUMxi=0;
        double SUMxx=0;
        double SUMlny=0;
        double SUMxlny=0;
        double n=x.size();
        double mis= 0;
        for (int i=0;i<x.size();i++) {
           SUMxi+=x.get(i);
            SUMxx+=Math.pow(x.get(i),2);
            SUMlny+=Math.log(y.get(i));
            SUMxlny+=x.get(i)*Math.log(y.get(i)); }
        double b=(SUMxlny*n-SUMxi*SUMlny)/(SUMxx*n-Math.pow(SUMxi,2));
        double a=SUMlny/n-SUMxi*(b/n);
        for (int i=0; i <x.size();i++) {
            mis+=Math.abs((y.get(i)-(Math.exp(a +b*x.get(i)))))/ y.get(i); }
        double mis1=mis*100/n;
        System.out.println("Среднее отклонение расчетных значений от фактических (экспоненциальная регрессия) ="+mis1);
        XYChart.Series<Double,Double> series3=new XYChart.Series<>();
        for (int i=0;i<x.size();i++) {
        series3.getData().add(new XYChart.Data(x.get(i),Math.exp(a+b*x.get(i)))); }
        lineChart.getData().add(series3);
         series3.setName("Экспоненциальная регрессия");
        return mis1;
    }


     public static double QuadReg(ArrayList<Double> x, ArrayList<Double> y, LineChart lineChart) {double n = x.size();
       double SUMy=0;
        double SUMx=0;
        double n=x.size();
        double mis=0;
        for (int i=0;i<x.size();i++) {
          SUMx+=x.get(i);
          SUMy+=y.get(i); }
        double a=-78.4912;
        double b=307363.8801;
        double c=-299569600.6046;
        XYChart.Series<Double, Double> series4 = new XYChart.Series<>();
        for (int i=5; i<x.size()-5;i++) {
           series4.getData().add(new XYChart.Data(x.get(i),a*Math.pow(x.get(i),2)+b*x.get(i)+c));  }
        for (int i=0; i<x.size();i++) {
           mis+=Math.abs((y.get(i)-(a*x.get(i)*x.get(i) + b*x.get(i) +c))/y.get(i)); }
        double mis1=mis*100/n;
        System.out.println("Среднее отклонение расчетных значений от фактических (квадратичная регрессия) ="+mis1);
        lineChart.getData().add(series4);
        series4.setName("Квадратичная регрессия");
        return mis1;
        }

}
