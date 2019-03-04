package ca.nines.alfred;

import ca.nines.alfred.main.Main;

public class Test {

    public static void main(String[] args) {
        Main m = new Main();
        String[] testArgs = {"html", "report.xml"};
        m.run(testArgs);
    }

}
