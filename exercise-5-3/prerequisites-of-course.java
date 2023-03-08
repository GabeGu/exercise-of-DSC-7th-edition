import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class prerequisitesOfCourse {
    public static void main(String[] args) {
        /* Take a course_id value from the keyboard */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a course_id: ");
        String course_id = scanner.nextLine();

        try {
            String q;
            ArrayList<String> c_prereq = new ArrayList<String>();
            ArrayList<String> new_c_prereq = new ArrayList<String>();
            ArrayList<String> temp = new ArrayList<String>();
            ResultSet result;

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:star/X@//edgar.cse.lehigh.edu:1521/XE");
            /*Compare to the answer, use the "in" clause and the con.createStatement() 
             * can improve the performance of the SQL query:
             * q = "select prereq_id from prereq where in (" + course_id_stringList + ")";
             */
            q = "select prereq_id from prereq where course_id = ?";
            Statement stmt = con.prepareStatement();
            /*Finds prerequisites of that course using an SQL query submitted via JDBC */
            stmt.setString(1, course_id);
            result = stmt.executeQuery(q);
            if (result.next()) {
                do {
                    new_c_prereq.add(result.getString("prereq_id"));
                } while (result.next());
                /*For each course returned, finds its prerequisites and continues this process interatively
                 * until no new prerequisite courses are found
                */
                do {
                    c_prereq.addAll(new_c_prereq);
                    for (String str: new_c_prereq) {
                        stmt.setString(1, str);
                        result = stmt.executeQuery(q);
                        while (result.next()) {
                            String prereq_id = result.getString("prereq_id");
                            if (!c_prereq.contains(prereq_id)) { // new prereq_id
                                temp.add(prereq_id);
                            }
                        }
                    }
                    new_c_prereq.clear();
                    new_c_prereq.addAll(temp); // all the new prereq_id in this interation
                } while (!new_c_prereq.isEmpty());
            }
            else {
                System.out.println(course_id + " is not a valid value");
            }
            /*Print out the result */
            for (String string : c_prereq) {
                System.out.println(string);
            }
            result.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
