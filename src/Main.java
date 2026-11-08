import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// папки и файл со ссылками
private static final String LAB = System.getProperty("user.home") + "\\Documents\\Lab\\";
private static final String FILE = LAB + "inFile.txt";
private static final String MUSIC = LAB + "music\\";
// ссылки на музыку
private static final String SONG1 = "https://ru-d3.drivemusic.me/dl/cQ2dNdbXatfOuuhyeEFxfg/1794181953/download_music/2023/10/cupsize-pjatyjj-jelement.mp3";
private static final String SONG2 = "https://ru-d1.drivemusic.me/dl/PlfGsq4381wEusjFj4m8wA/1794182050/download_music/2023/12/cupsize-semnadcatiletnjaja.mp3";
private static final String SONG3 = "https://ru-d1.drivemusic.me/dl/GNVMI1QGA4bduUUEdNSaQA/1794182282/download_music/2023/12/cupsize-oni-vse-drochat-na-tebja-v-internete.mp3";
// плеер и список процессов
static Process player = null;
static String songName = "";
static List<Long> processes = new ArrayList<>();
void main() {
    System.out.println("выберите действие:");
    System.out.println(" 1 - скачать всю музыку \n 2 - скачать пятый элемент \n 3 - скачать семнадцатилетняя \n 4 - скачать они все *** на тебя в интернете");
    System.out.println("музыка сохранится в: " + MUSIC);
    System.out.print("ваш выбор:");
    try {
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        new File(MUSIC).mkdirs();
        createFile();
        if (choice == 1) {
            downloadAll(FILE);
        }
        else if (choice == 2) {
            playSong(SONG1, "pjatyjj_jelement.mp3", "пятый элемент");
        }
        else if (choice == 3) {
            playSong(SONG2, "semnadcatiletnjaja.mp3", "семнадцатилетняя");
        }
        else if (choice == 4) {
            playSong(SONG3, "oni_vse__na_tebja_v_internete.mp3", "они все *** на тебя в интернете");
        }
        else {
            System.out.println("неверный выбор");
        }
    }
    catch (InputMismatchException e) {
        System.out.println("нужно ввести число от 1 до 4");
    }
    showInfo();
}
// создаём файл со ссылками
static void createFile() {
    File file = new File(FILE);
    if (file.exists()) {
        return;
    }
    file.getParentFile().mkdirs();

    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
        writer.println("https://ru-d3.drivemusic.me/dl/cQ2dNdbXatfOuuhyeEFxfg/1794181953/download_music/2023/10/cupsize-pjatyjj-jelement.mp3");
        writer.println("https://ru-d1.drivemusic.me/dl/PlfGsq4381wEusjFj4m8wA/1794182050/download_music/2023/12/cupsize-semnadcatiletnjaja.mp3");
        writer.println("https://ru-d1.drivemusic.me/dl/GNVMI1QGA4bduUUEdNSaQA/1794182282/download_music/2023/12/cupsize-oni-vse-drochat-na-tebja-v-internete.mp3");

        System.out.println("создан файл со ссылками");
    }
    catch (IOException e) {
        System.out.println("ошибка создания файла: " +e.getMessage());
    }
}
//скачиваем все песни из файла
static void downloadAll(String way) {
    System.out.println("\nчитаем ссылки и скачиваем музыку...");
    int count = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(way))) {
        String url;

        while ((url = reader.readLine()) != null) {
            url = url.trim();
            if (url.isEmpty()) {
                continue;
            }
            try {
                download(url, MUSIC + "song_" +count + ".mp3");
                System.out.println("скачано:song_" +count+1 + ".mp3");
                count++;
            }
            catch (IOException | IllegalArgumentException e) {
                System.out.println("ошибка ссылки: " +e.getMessage());
            }
        }
    }
    catch (FileNotFoundException e) {
        System.out.println("файл не найден: " +way);
    }
    catch (IOException e) {
        System.out.println("ошибка чтения: " +e.getMessage());
    }
    System.out.println("всего скачано песен:" +count);
}
// скачиваем одну песню и запускаем её
static void playSong(String url, String name, String title) {
    String path = MUSIC + name;
    try {
        System.out.println("\nскачиваем " + title + "...");
        download(url, path);

        System.out.println("скачано: " +path);
        System.out.println("размер: " +new File(path).length() + " байт");
        System.out.println("запускаем плеер...");

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "/wait", "", new File(path).getAbsolutePath());
        player = pb.start();
        songName = title;
        processes.add(player.pid());
    }
    catch (IOException | IllegalArgumentException e) {
        System.out.println("ошибка: " +e.getMessage());
    }
}
// скачивание файла
static void download(String url, String file) throws IOException {
    try (ReadableByteChannel input = Channels.newChannel(
         URI.create(url).toURL().openStream());
         FileOutputStream output = new FileOutputStream(file)) {
        output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
    }
}
// вывод информации о процессах
static void showInfo() {
    System.out.println("\nинформация о процессах:");

    if (player != null) {
        System.out.println("песня: " + songName);
        System.out.println("PID: " + player.pid());
        System.out.println("активен: " + player.isAlive());
    }
    ProcessHandle current = ProcessHandle.current();

    System.out.println("текущий PID:" + current.pid());
    System.out.println("активен:" + current.isAlive());
    System.out.println("дочерние процессы:" + current.children().count());
    System.out.println("информация:" + current.info());
    System.out.println("список PID:" + processes);
}