package com.example.fbtestapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {
    private List<UserData> userDataList;

    RecyclerAdapter(List<UserData> userDataList){this.userDataList = userDataList;}

    void updateData(List<UserData> list){
        userDataList.clear();
        userDataList.addAll(list);
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView name, surname, gender, age;

        MyHolder(View view){
            super(view);
            name = view.findViewById(R.id.rec_name);
            surname = view.findViewById(R.id.rec_surname);
            gender = view.findViewById(R.id.rec_gender);
            age = view.findViewById(R.id.rec_age);
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_obj, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        UserData userData = userDataList.get(position);
        String head = userData.getId() + " " + userData.getName();
        holder.name.setText(head);
        holder.surname.setText(userData.getSurname());
        holder.gender.setText(userData.getSex());
        holder.age.setText(String.valueOf(userData.getAge()));
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }
}
