import java.io.*;
import java.util.*;

public class Main {

	public static final String ADB_ROOT = "/usr/local/Caskroom/android-sdk/3859397,26.0.1/platform-tools/";
	public static final String ADB = ADB_ROOT + "adb ";
	public static final String ADB_INPUT = ADB_ROOT + "adb shell input ";
	public static final String TELNET_TOKEN = "wVlAD+WhhLkin2GM";
	public static final String EMULATOR_PORT = "5554";

	private static BufferedWriter connectToTelnet() throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process telnet = rt.exec("telnet localhost " + EMULATOR_PORT);
		return new BufferedWriter(new OutputStreamWriter(telnet.getOutputStream()));
	}

	public static void rotate() throws IOException {
		BufferedWriter out = connectToTelnet();
		out.write("auth " + TELNET_TOKEN + "\n");
		out.write("rotate\n");
		out.write("quit\n");
		out.flush();
	}

	public static void changeSpeed() throws IOException {
		List<String> speedList = Arrays.asList("gsm", "hscsd", "gprs", "edge", "umts", "hsdpa", "lte", "evdo", "full");
		BufferedWriter out = connectToTelnet();
		out.write("auth " + TELNET_TOKEN + "\n");
		String newSpeed = speedList.get((new Random(985)).nextInt(speedList.size()));
		System.out.println("> Changing network speed to " + newSpeed);
		out.write("network speed " + newSpeed + "\n");
		out.write("quit\n");
		out.flush();
	}

	public static void changeSensor() throws IOException {
		Random random = new Random(7582);
		List<String> sensorList = Arrays.asList("acceleration", "gyroscope", "magnetic-field", "orientation",
				"temperature", "proximity", "light", "pressure", "humidity", "magnetic-field-uncalibrated",
				"gyroscope-uncalibrated");
		BufferedWriter out = connectToTelnet();
		out.write("auth " + TELNET_TOKEN + "\n");
		String sensor = sensorList.get(random.nextInt(sensorList.size()));
		String values = random.nextInt(1000) + ":"+ random.nextInt(1000) + ":" + random.nextInt(1000);
		System.out.println("> Changing " + sensor + " to " + values);
		out.write("sensor set " + sensor + " " + values + "\n");
		out.write("quit\n");
		out.flush();
	}

	public static void main(String[] args) {
		String[] commands = { "tap", "text", "swipe", "keyevent", "rotate", "network", "sensor" };
		double[] probabilities = { 0.143, 0.143, 0.143, 0.143, 0.143, 0.143, 0.143 };
		System.out.println("Starting");
		String app = null;
		int eventsNumber = 0;
		System.out.println("Processing args:");
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].replaceAll("-", "");
			System.out.println(arg);

			switch (arg) {
			case "app":
				app = args[++i];
				break;
			case "c":
				commands = args[++i].split(",");
				probabilities = new double[commands.length];
				double probability = 1/commands.length;
				for(int j = 0; j < commands.length; j++) {
					probabilities[j] = probability;
				}
				break;
			case "cp":
				String[] commandsP = args[++i].split(",");
				commands = new String[commandsP.length];
				probabilities = new double[commandsP.length];
				for(int j = 0; j < commandsP.length; j++) {
					String[] commandP = commandsP[j].split(":");
					commands[j] = commandP[0];
					probabilities[j] = Double.parseDouble(commandP[1]);
				}
				break;
			default:
				eventsNumber = Integer.parseInt(arg);
				break;
			}
		}
		double probSum = 0;
		for(double p:probabilities) 
			probSum += p;
		if(probSum > 1.01) {
			System.out.println("Probabilities sum cannot be greater than 1: " + probSum);
			System.exit(1);
		}
		System.out.println("");
		System.out.println("Running with ");
		for(int i = 0; i < commands.length; i++) {
			System.out.print(commands[i] + ":" + probabilities[i] + " ");
		}
		System.out.println("");
		System.out.println("");
		Runtime rt = Runtime.getRuntime();
		try {
			if (app != null) {
				String cmd = ADB + "install " + app;
				System.out.println("# Running " + cmd);
				Process p = rt.exec(cmd);
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while ((line = in.readLine()) != null) {
					System.out.println(line);
				}
				in.close();
				BufferedReader in2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = in2.readLine()) != null) {
					System.out.println(line);
				}
				in2.close();
				p.waitFor();
				System.out.println("Install successful, running app...");
				cmd = ADB + "shell monkey -p " + app.replaceAll(".apk", "") + " -c android.intent.category.LAUNCHER 1";
				System.out.println("# Running " + cmd);
				rt.exec(cmd).waitFor();
				Thread.sleep(2000);
			}
			Random random = new Random(12345);

			System.out.println("Launching monkey");
			int i = 1;
			while (i <= eventsNumber) {
				int index = random.nextInt(commands.length);
				if (random.nextInt((int) Math.ceil(1 / probabilities[index])) + 1 == 1) {
					System.out.println("Event " + i + ": " + commands[index]);
					String cmd = "";
					switch (commands[index]) {
					case "tap":
						int x = random.nextInt(1080);
						int y = random.nextInt(1920);
						cmd = ADB_INPUT + "tap " + x + " " + y;
						System.out.println("> " + cmd);
						rt.exec(cmd);
						break;
					case "text":
						String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
						int len = random.nextInt(30);
						StringBuilder sb = new StringBuilder(len);
						for (int k = 0; k < len; k++)
							sb.append(chars.charAt(random.nextInt(chars.length())));
						String randomString = sb.toString();
						
						cmd = ADB_INPUT + "text " + randomString;
						System.out.println("> " + cmd);
						rt.exec(cmd);
						break;
					case "swipe":
						int ix = random.nextInt(1080);
						int iy = random.nextInt(1920);
						int fx = random.nextInt(1080);
						int fy = random.nextInt(1920);
						cmd = ADB_INPUT + "swipe " + ix + " " + iy + " " + fx + " " + fy;
						System.out.println("> " + cmd);
						rt.exec(cmd);
						break;
					case "keyevent":
						cmd = ADB_INPUT + "keyevent " + random.nextInt(284);
						System.out.println("> " + cmd);
						rt.exec(cmd);
						break;
					case "rotate":
						Main.rotate();
						break;
					case "network":
						Main.changeSpeed();
						break;
					case "sensor":
						Main.changeSensor();
						break;
					}
					i++;
					Thread.sleep(500);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
