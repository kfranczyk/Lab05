package pollub.ism.lab05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button butZapisz=null, butOdczyt=null;
    private EditText nazwaZapis=null, notatka=null;
    private Spinner nazwaCzytaj =null;

    private ArrayList<String> nazwyPlikow = null;
    private ArrayAdapter<String> adapterSpinnera = null;

    private final String NAZWA_PREFERENCES = "Aplikacja do notatek";
    private final String KLUCZ_DO_PREFERENCES = "Zapisane nazwy plików";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butZapisz = (Button) findViewById(R.id.przyciskZapisz);
        butOdczyt = (Button) findViewById(R.id.przyciskCzytaj);
        nazwaZapis = (EditText) findViewById(R.id.editTextNazwaZapisz);
        notatka = (EditText) findViewById(R.id.editTextNotatka);
        nazwaCzytaj = (Spinner) findViewById(R.id.spinnerNazwaCzytaj);


        butZapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisanieNotatki();
            }
        });
        butOdczyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odczytanieNotatki();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        zapiszSharedPreference();
    }

    @Override
    protected void onResume() {
        super.onResume();

        nazwyPlikow = new ArrayList<>();
        adapterSpinnera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nazwyPlikow);
        adapterSpinnera.setDropDownViewResource(R.layout.spinner);
        nazwaCzytaj.setAdapter(adapterSpinnera);

        odczytajSharedPreference();
    }

    private void zapisanieNotatki(){
        String nazwaPliku = nazwaZapis.getText().toString();
        String informacja = "Udało się zapisać";

        if(!zapiszDoPliku(nazwaPliku,notatka))
            informacja="nie udało się zapisać";

        Toast.makeText(this,informacja,Toast.LENGTH_LONG);
    }
    private void odczytanieNotatki(){
        String nazwaPliku = nazwaCzytaj.getSelectedItem().toString();
        String informacja = "Udało się przeczytać";

        if(!odczytajZPliku(nazwaPliku,notatka))
            informacja = "Nie udało się przeczytać";

        Toast.makeText(this,informacja,Toast.LENGTH_LONG);
    }
    private void zapiszSharedPreference(){
        SharedPreferences preferences = getSharedPreferences(NAZWA_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor edytor = preferences.edit();
        edytor.putStringSet(KLUCZ_DO_PREFERENCES, new HashSet<>(nazwyPlikow));
        edytor.apply();
    }
    private void odczytajSharedPreference(){
        SharedPreferences preferences = getSharedPreferences(NAZWA_PREFERENCES,MODE_PRIVATE);
        Set<String> zapisaneNazwy = preferences.getStringSet(KLUCZ_DO_PREFERENCES,null);

        if(zapisaneNazwy!=null){
            nazwyPlikow.clear();
            for(String nazwa: zapisaneNazwy)
                nazwyPlikow.add(nazwa);
            adapterSpinnera.notifyDataSetChanged();
        }
    }
    private boolean zapiszDoPliku(String nazwaPliku, EditText poleEdycyjne){
        boolean sukces = true;
        File katalog = getApplicationContext().getExternalFilesDir(null);
        File plik = new File(katalog + File.separator + nazwaPliku);
        BufferedWriter zapisywacz = null;

        try{
            zapisywacz = new BufferedWriter(new FileWriter(plik));
            zapisywacz.write(poleEdycyjne.getText().toString());
        }catch (Exception e){
            sukces= false;
        }finally{
            try {
                zapisywacz.close();
            }catch (Exception e){
                sukces=false;
            }
        }

        if(sukces && !nazwyPlikow.contains(nazwaPliku)){
            nazwyPlikow.add(nazwaPliku);
            adapterSpinnera.notifyDataSetChanged();
        }

        return sukces;
    }
    private boolean odczytajZPliku(String nazwaPliku, EditText poleEdycyjne){
        boolean sukces = true;
            File katalog =getApplicationContext().getExternalFilesDir(null);
            File plik = new File(katalog + File.separator + nazwaPliku);
            BufferedReader odczytywacz = null;

            if( plik.exists() ) {
                try {
                    odczytywacz = new BufferedReader(new FileReader(plik));
                    String linia = odczytywacz.readLine() +"\n";
                    while(linia!=null){
                        poleEdycyjne.getText().append(linia);
                        linia = odczytywacz.readLine().concat("\n");

                    }
                }catch (Exception e){
                    sukces = false;
                }finally {
                    if(odczytywacz!=null){
                        try {
                            odczytywacz.close();
                        }catch (Exception e){
                            sukces = false;
                        }
                    }
                }
            }

        return sukces;
    }
}