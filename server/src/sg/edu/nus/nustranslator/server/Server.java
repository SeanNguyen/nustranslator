package sg.edu.nus.nustranslator.server;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

class Server {

	private static AudioFormat audioFormat = new AudioFormat(Configurations.sampleRate, 16, 1, true, false);;
	
	public static void main(String args[]) throws Exception {
		System.out.println("START");
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(Configurations.port);
		Socket socket = serverSocket.accept();
		System.out.println("GOT CONNECTION");
		DataInputStream in = new DataInputStream(socket.getInputStream());
		int count = 0;
		
		while (Configurations.status) {
			int dataLength = in.readInt();
			if (dataLength > 0) {
				byte[] message = new byte[dataLength];
				in.readFully(message, 0, message.length);
				
				System.out.println(count++);
				Thread playAudio = new Thread(new Runnable() {
					@Override
					public void run() {
						toSpeaker(message);
					}
				});
				playAudio.start();
			}
		}
	}
	
	public static void toSpeaker(byte soundbytes[]) {
	    try {

	        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
	        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

	        sourceDataLine.open(audioFormat);

	        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
	        volumeControl.setValue(5);

	        sourceDataLine.start();
	        sourceDataLine.open(audioFormat);

	        sourceDataLine.start();

	        //System.out.println("format? :" + sourceDataLine.getFormat());

	        sourceDataLine.write(soundbytes, 0, soundbytes.length);
	        //System.out.println(soundbytes.toString());
	        sourceDataLine.drain();
	        sourceDataLine.close();
	    } catch (Exception e) {
	        System.out.println("Not working in speakers...");
	        e.printStackTrace();
	    }
	}
}