package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;

import com.myhealthplusplus.app.Models.Sick_Rows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IfYoureFeelingSick extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Sick_Rows> sick_rowsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if_youre_feeling_sick);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "More Info" + "</font>"));

        recyclerView = findViewById(R.id.sick_recyclerView);

        initData();
        setRecyclerView();
    }

    private void setRecyclerView() {
        Sick_Rows_Adapter adapter = new Sick_Rows_Adapter(sick_rowsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        sick_rowsList = new ArrayList<>();

        sick_rowsList.add(new Sick_Rows("1. Stay home and call a health care provider", "Unless it is an emergency, to reduce your risk of catching or spreading illness, stay home if you feel sick, even if your symptoms are mild. Do not go to work, school or public places, and avoid public transportation. If your symptoms are severe or you feel like you need medical care, call before you go to your health care provider. Describe your symptoms over the phone.\n\n" +
                "If you have a medical emergency, call 1999 and tell the dispatcher about your symptoms."));
        sick_rowsList.add(new Sick_Rows("2. Answer questions to determine your risk", "When you call a health care facility, you will be asked about your risks for COVID-19. Risk factors include being unvaccinated, attending indoor events, recent travel to certain areas or exposure to an infected person.\n\n" + "For instance,\n\n" + "# Have you had close contact with someone diagnosed with COVID-19, the disease caused by the new coronavirus? (Close contact means having been within 6 feet of that person for an extended time or being exposed to their cough or sneeze.)\n\n" + "# Do you have a cough, fever or chills, shortness of breath or difficulty breathing, muscle or body aches, sore throat, new loss of taste or smell, diarrhea, headache, new fatigue, nausea or vomiting, or congestion or runny nose?\n\n" + "# Has a public health officer said you were potentially exposed to COVID-19?"));
        sick_rowsList.add(new Sick_Rows("3. Follow your health care provider’s instructions", "Based on your answers to these questions, the care provider will provide instructions over the phone. You will be told if you need to be evaluated, and if so, what to do next. Based on your risk for COVID-19, your health care provider may recommend that you:\n\n" + "# Continue to monitor your health and call back if you develop a fever or respiratory symptoms.\n\n" + "# Stay home and await further instructions." + "# Report to a designated medical care facility for evaluation and treatment. It’s best to go alone to your appointment. Do not bring children or other family members unless you need assistance.\n\n" + "# Go to a clinic or emergency department if you have more severe symptoms, such as shortness of breath."));
        sick_rowsList.add(new Sick_Rows("4. Practice hand hygiene and respiratory etiquette", "# If you do leave your home to go to a care facility, wear a mask so droplets from your breath, coughs and sneezes are less likely to infect others.\n\n" + "# Wash your hands thoroughly (for at least 20 seconds) after sneezing, blowing your nose, coughing or using the bathroom, and before preparing or eating food.\n\n" + "# If you cough or sneeze, do so into the bend of your elbow, not your hand. Or use a tissue, and then throw it away immediately afterward.\n\n" + "# At home, clean often-touched surfaces such as doors and doorknobs, cabinet handles, bathroom hardware, tabletops, phones, tablets and keyboards regularly with disinfectant."));
        sick_rowsList.add(new Sick_Rows("5. Stay calm", "The possibility of having a contagious illness can be scary, but doctors, nurses and other caregivers can help provide care to patients while avoiding spread of the illness in the community."));
        sick_rowsList.add(new Sick_Rows("6. Consider being vaccinated for COVID-19", "Once you feel better, talk to your doctor or health professional about getting vaccinated for the coronavirus. The COVID-19 vaccines are safe and effective, and the U.S. Centers for Disease Control and Prevention (CDC) recommends that people who have already had COVID-19 or tested positive should still get the COVID-19 vaccination. Studies show that vaccination provides a strong boost in protection in people who have recovered from COVID-19."));
    }
}