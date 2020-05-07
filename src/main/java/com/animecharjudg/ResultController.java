package com.animecharjudg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.StringTokenizer;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ResultController {

    @Autowired
    ResourceLoader resourceLoader;

    @ModelAttribute
    public ImageForm setForm() {
        return new ImageForm();
    }

    static String currentDir = "/home/syoum/Python/";
    //static String currentDir = "C:/Users/syoum/pleiades/workspace/Python/";

    @PostMapping("/result")
    public String upload(ImageForm imageForm, Model model) throws Exception {

        String uploadPath = currentDir + "uploadfile/" + imageForm.getImage().getOriginalFilename();
        String csvPath = currentDir + "result/" + "result.csv";
        String imgPath = currentDir + "result/" + "result.png";

        //upload画像の保存
        saveFile(imageForm.getImage(), uploadPath);

        //画像解析処理
        testPy(uploadPath);

        //プレビュー画像の読み込み
        StringBuffer data = new StringBuffer();
        String base64 = new String(Base64.encodeBase64(loadFile(imgPath)));
        data.append("data:image/jpeg;base64,");
        data.append(base64);

        //解析結果の読み込み
        String[] stts_img = openCSV(csvPath, 3);

        //画面への引き渡し
        model.addAttribute("preview64image", data.toString());
        model.addAttribute("fileName", "ファイル名：" + stts_img[0]);
        model.addAttribute("charaName", "キャラ名：" + stts_img[1]);
        model.addAttribute("recognition", "認識率：" + stts_img[2]);

        return "02_Result";
    }


    /*
     * test.pyの呼び出し
     * @param path 画像ファイルのフルパス
     */
    public static void testPy(String path) throws IOException {
    	Runtime runtime = Runtime.getRuntime();

    	// cmd.exeでmecab.exeを起動し指定したファイル(filePath)を形態素解析する
    	//String[] Command = { "/bin/sh", "-c", "python /home/syoum/Python/test.py " + path};
    	//String[] Command = { "cmd", "/c", "python test.py " + path};

    	// 実行ディレクトリの指定
    	File dir = new File(currentDir);
		Process p = null;
    	try {
			ProcessBuilder processBuilder = new ProcessBuilder("/home/syoum/.pyenv/shims/python", "/home/syoum/Python/test.py", path);
			p = processBuilder.start();

    		// 実行ディレクトリ(dir)でcommand(mecab.exe)を実行する
    		//p = runtime.exec("python /home/syoum/Python/test.py " + path, null, dir);
    		//p = runtime.exec(Command, null, dir);



    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	try {
    		int ret = p.waitFor(); // プロセスの正常終了まで待機させる
			System.out.println("戻り値:" + ret);
			InputStream is = p.getInputStream();	//標準出力
			printInputStream(is);
			InputStream es = p.getErrorStream();	//標準エラー
			printInputStream(es);

			p.waitFor();

    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

    /*
     * csvの読み込み（先頭1行のみ取得）
     * @param path CSVファイルのフルパス
     * @param maxColumnNum 取得する列数
     */
    public static String[] openCSV(String path, int maxColumn) {

        try {
            //ファイルを読み込む
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String[] txt = new String[maxColumn];

            //読み込んだファイルを１行ずつ処理する
            String line;
            StringTokenizer token;
            while ((line = br.readLine()) != null) {
                //区切り文字","で分割する
                token = new StringTokenizer(line, ",");

                //分割した文字を格納する
                int tokenNum = 0;
                while (token.hasMoreTokens()) {
                    txt[tokenNum] = token.nextToken();
                    tokenNum++;
                }
            }

            //終了処理
            br.close();
            return txt;

        } catch (IOException ex) {
            //例外発生時処理
            ex.printStackTrace();
    		return null;
        }
    }

    /*
     * ファイルの保存
     * @param path 格納場所（保存するファイル名を含めたフルパス）
     */
    private void saveFile(MultipartFile file, String path) {
        Path uploadfile = Paths.get(path);
        try (OutputStream os = Files.newOutputStream(uploadfile, StandardOpenOption.CREATE)) {
          byte[] bytes = file.getBytes();
          os.write(bytes);
        } catch (IOException ex) {
            //例外発生時処理
            ex.printStackTrace();
        }
      }

    /*
     * 外部の画像ファイルの読み込み
     * @param path 画像ファイルのフルパス
     * @return byte[]
    */
    private byte[] loadFile(String path) throws IOException {
        try {
            Resource resource = resourceLoader.getResource("file:" + path);
            File file = resource.getFile();
            byte[] bt = Files.readAllBytes(file.toPath());;

            return bt;

        } catch (IOException ex) {
            //例外発生時処理
            ex.printStackTrace();
    		return null;
        }
    }


	public static void printInputStream(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			for (;;) {
				String line = br.readLine();
				if (line == null) break;
				System.out.println(line);
			}
		} finally {
			br.close();
		}
	}

}