package com.roote.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.roote.entity.Importer;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.util.Log;

public class ReadCSV {
	
	private int cityVille = -1;
	private int companyEntreprise = -1;
	private int province = -1;
	private int postalCode = -1;

	public int getCityVille() {
		return cityVille;
	}

	public void setCityVille(int cityVille) {
		this.cityVille = cityVille;
	}

	public int getCompanyEntreprise() {
		return companyEntreprise;
	}

	public void setCompanyEntreprise(int companyEntreprise) {
		this.companyEntreprise = companyEntreprise;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}

	@SuppressLint("DefaultLocale")
	public ArrayList<Importer> getImporters(AssetManager assetManager) {
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<String> csvFiles = new ArrayList<String>();
		for(int i = 0; i < files.length; i++){
			if(files[i].toLowerCase().contains("csv")){
				csvFiles.add(files[i]);
			}
		}
		
		ArrayList<Importer> importers = new ArrayList<Importer>();
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		for(String csvFile: csvFiles){
			try {
				br = new BufferedReader(new InputStreamReader(assetManager.open(csvFile)));
				// Clear variables
				boolean firstLine = true;
				setCityVille(-1);
				setCompanyEntreprise(-1);
				setProvince(-1);
				setPostalCode(-1);
				while ((line = br.readLine()) != null) {
			 
					// use comma as separator
					String[] data = line.split(cvsSplitBy);
					
					if(firstLine == true){
						for(int i = 0; i < data.length; i++){
							if(data[i].equals("\"CITY-VILLE\"")){
								setCityVille(i);
							}else if(data[i].equals("\"COMPANY-ENTREPRISE\"")){
								setCompanyEntreprise(i);
							}else if(data[i].equals("\"PROVINCE_ENG\"")){
								setProvince(i);
							}else if(data[i].equals("\"POSTAL_CODE-CODE_POSTAL\"")){
								setPostalCode(i);
							}	
						}
						firstLine = false;
						continue;
					}
					Importer importer = new Importer();
					if(getCityVille() != -1){
						if(//data[getCityVille()].equals("\"Vancouver\"") ||
						   data[getCityVille()].equals("\"Toronto\"") 
						   //data[getCityVille()].equals("\"Montr�al\"")
						   ){
							importer.setCityVille(data[getCityVille()]);
						}else{
							continue;
						}
					}
					if(getCompanyEntreprise() != -1){
						data[getCompanyEntreprise()] = data[getCompanyEntreprise()].substring(1);
						data[getCompanyEntreprise()] = data[getCompanyEntreprise()].substring(0, data[getCompanyEntreprise()].length()-1);
						importer.setCompanyName(data[getCompanyEntreprise()]);
					}
					if(getProvince() != -1){
						importer.setProvince(data[getProvince()]);
					}
					if(getPostalCode() != -1){
						importer.setPostalCode(data[getPostalCode()]);
					}
					importers.add(importer);		
				}
				Log.i("CSV", Integer.toString(importers.size()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return importers;
	}
	
}
