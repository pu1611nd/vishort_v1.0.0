package com.tuan1611pupu.vishort.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Democontents {


    private Democontents() {
    }



    public static List<Song> getSongFiles() {
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("1", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FStudio_Project_V0.mp3?alt=media&token=19900314-d25a-491f-9acc-2f2b1dc697fa", "Nguyễn Đình Vũ", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2Fdoi%20lua.jpeg?alt=media&token=20b41d61-a8a8-4031-b140-f241e25fd58b",
                "0", "Dối Lừa", 2552));

        songs.add(new Song("2", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2Ftrach_duyen_trach_phan.mp3?alt=media&token=2d2f2bd0-0917-40ea-b502-03974c2cecb5", "Đỗ Thành Duy",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2Ftrach_duyen_trach_phan.jpeg?alt=media&token=dc4873f0-f68d-46de-a1b2-0589dcf7ec69",
                "1", "Trách duyên trách phận", 64674));

        songs.add(new Song("3", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FungQuaTroi.mp3?alt=media&token=9a338f81-6a75-498f-9163-20773b13f66f", "AMEE",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FungQuachung.jpeg?alt=media&token=f2e8ed19-df44-4a41-aa2b-67ba6dec4e4b",
                "2", "Ưng quá chừng", 78388));

        songs.add(new Song("4", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FnamKhuoc.mp3?alt=media&token=86ee4c70-50fa-4b3e-8e9e-5ba882bde701", "Bình Sinh Bất Vãn",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FnanKhuoc.jpeg?alt=media&token=53ba65e8-3063-4aa8-85c8-8f98387f85e2",
                "3", "Nan Khước", 78388));
        songs.add(new Song("5", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FbatTinhYeuLen.mp3?alt=media&token=bec7413c-0522-4c55-9711-6acb1e94ee02", "Hoà Minzy - Tăng Duy Tân",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FbatTinhYeuLen.jpeg?alt=media&token=da59ac61-0b18-42af-8ae3-22ad63598319",
                "4", "Bật tình yêu lên", 78388));
        songs.add(new Song("4", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FtayDuky.mp3?alt=media&token=aef78b3f-4df5-45ef-8a7a-702150f628a5", "Lê Bảo",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FTayduky.jpeg?alt=media&token=543467ff-a21b-4aa8-b947-9eae1284a2b2",
                "3", "Tây du ký remix", 78388));
        songs.add(new Song("4", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FloiYeuThuong.mp3?alt=media&token=5e2065f1-6daf-470c-903e-643fc5af7751", "Khải Đăng",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FtinhYeuCoTontai.jpeg?alt=media&token=8167a33e-6190-4ee4-8c59-f2fb356413a6",
                "3", "Tình yêu có tồn tại", 78388));
        songs.add(new Song("4", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FGio.mp3?alt=media&token=c51b3ca0-b70b-42e6-b412-5a70425c248e", "Jack",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2FGio.jpeg?alt=media&token=ef38e714-e415-42f8-a0be-c666d37f64ac",
                "3", "Gió", 78388));
        songs.add(new Song("4", "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/audio%2FmuaOi.mp3?alt=media&token=3bf8ef19-875f-4e71-a59d-f1c2b88e7d39", "Cầm",
                "https://firebasestorage.googleapis.com/v0/b/okokoi.appspot.com/o/image%2Fmuao%CC%9Bimuadungroi.jpeg?alt=media&token=bb8dd762-8e70-4740-a369-98da856a25bf",
                "3", "Mưa ơi đừng rơi ", 78388));



        return songs;
    }




}
