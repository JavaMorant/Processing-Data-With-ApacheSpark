//CS1003P4
//15 April 2022
//
public class CS1003P4 {
    public static void main(String[] args) {

        // Passing in command line argumentz
        BackEnd backEnd = new BackEnd(args[0], args[1],
                Double.valueOf(args[2]));
        backEnd.Search();
    }
}