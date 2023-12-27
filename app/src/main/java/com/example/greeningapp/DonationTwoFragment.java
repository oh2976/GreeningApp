package com.example.greeningapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DonationTwoFragment extends Fragment{
    private RecyclerView recyclerView_donation_two;
    private RecyclerView.Adapter doantionAdapter_two;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Donation> donationArrayList_two;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_donation_two, container, false);

        recyclerView_donation_two = view.findViewById(R.id.recyclerView_donation_two); //아이디 연결
        recyclerView_donation_two.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_donation_two.setLayoutManager(layoutManager);
        donationArrayList_two = new ArrayList<>(); //item_list 담을 어레이 리스트 (어댑터 쪽으로)

        // 파이어베이스 경로 설정
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donation");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                donationArrayList_two.clear();

                long currentTimeMillis = System.currentTimeMillis();

                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Donation donation = snapshot.getValue(Donation.class);

                    // 기부 시작 날짜와 끝나는 날짜 가져오기
                    String donationStartDateString = donation.getDonationstart();
                    String donationEndDateString = donation.getDonationend();

                    // SimpleDateFormat을 사용하여 문자열을 Date 객체로 변환
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        Date donationStartDate = dateFormat.parse(donationStartDateString);
                        Date donationEndDate = dateFormat.parse(donationEndDateString);

                        // 기부 시작 시간 및 끝나는 시간 가져오기
                        long donationStartTime = donationStartDate.getTime();
                        long donationEndTime = donationEndDate.getTime();

                        if (!(currentTimeMillis >= donationStartTime && currentTimeMillis <= donationEndTime)) {
                            // 기부 불가능한 시간인 경우에만 리스트에 추가
                            donationArrayList_two.add(donation);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                doantionAdapter_two.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 오류 처리
            }
        });

        // 기부 프로젝트 어댑터 생성 후 리사이클러뷰에 어댑터 연결
        doantionAdapter_two = new CertificateAdapter(donationArrayList_two, getActivity());
        recyclerView_donation_two.setAdapter(doantionAdapter_two);

        return view;
    }
}
