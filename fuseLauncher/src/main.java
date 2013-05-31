/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/27/13
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class main {

    public static void main(String[] args){
        registerShutdownHook();
        while (true){
            System.out.println("vivo");
        }
    }

    public static void registerShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run(){
                //Add code here
                System.out.println("Mi hai ucciso");
            }

        });
    }

}
