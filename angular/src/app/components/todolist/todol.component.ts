import { Component, OnInit } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: "app-todoli",
  templateUrl: "./todol.component.html",
  styleUrls: ["./todol.component.less"]
})
export class ToDoLiComponent implements OnInit {
  constructor(private http: HttpClient) {}
  public toarry: [{ id: number, data: string, active: string, editby: string }];

  ngOnInit() {
    this.getAll();
  }

  private getAll(): void {
    this.http
      .get("http://192.168.2.10:8080/todolist/getAllJson")
      .subscribe((resdata: any) => {
        this.toarry = resdata;

        this.toarry.forEach(item => {
          console.log(item);
        });
      });
  }
}