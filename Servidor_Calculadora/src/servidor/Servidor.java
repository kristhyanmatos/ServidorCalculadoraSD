/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import bean.Dados;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kristhyanmatos
 */
public class Servidor {

    private ServerSocket servidor;
    private Socket socket;

    public static void main(String[] args) {
        new Servidor();

    }

    public Servidor() {
        try {
            servidor = new ServerSocket(7412);
            System.out.println("Servidor ON, esperando requisição!");
            while (true) {
                socket = servidor.accept();
                new Thread(new Calculadora(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Calculadora implements Runnable {

        Dados dados = new Dados();
        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;
        private Map<String, ObjectOutputStream> streamMap = new HashMap<String, ObjectOutputStream>();

        public Calculadora(Socket socket) {
            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Cliente: " + socket.getInetAddress().getHostName() + " Conectou!");
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        public void run() {
            try {
                while ((dados = (Dados) objectInputStream.readObject()) != null) {
                    streamMap.put(String.valueOf(dados.getOperacao()), objectOutputStream);
                    if (dados.getN1() != 0 && dados.getN2() != 0) {
                        for (Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
                            if (String.valueOf(dados.getOperacao()).equals(kv.getKey())) {
                                executa(dados.getN1(), dados.getN2(), dados.getOperacao());
                            }
                        }
                    } else {

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void executa(double n1, double n2, int operacao) {
            Dados dadosTratados = new Dados();
            switch (operacao) {
                //adicao
                case 1: {
                    dadosTratados.setResultado((n1+n2));
                    break;
                }
                //subtração
                case 2:{
                    dadosTratados.setResultado((n1-n2));
                    break;
                }
                //divisao
                case 3:{
                    dadosTratados.setResultado(n1*n2);
                    break;
                }
                //multiplicação
                case 4:{
                    dadosTratados.setResultado(n1/n2);
                    break;
                }
                default:{
                    System.out.println("sem operacao referente");
                    break;
                }
                
            }
            try {
                this.objectOutputStream.writeObject(dadosTratados);
                System.out.println("Dados enviados para o cliente");
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        public ObjectInputStream getObjectInputStream() {
            return objectInputStream;
        }

        public void setObjectInputStream(ObjectInputStream objectInputStream) {
            this.objectInputStream = objectInputStream;
        }

        public ObjectOutputStream getObjectOutputStream() {
            return objectOutputStream;
        }

        public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
            this.objectOutputStream = objectOutputStream;
        }

    }
}
