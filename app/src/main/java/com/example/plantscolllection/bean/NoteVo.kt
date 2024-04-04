package com.example.plantscolllection.bean

class NoteVo {

     var id:Int = -1

     var userId:Int? = null

     var name: String? = null

     var species: Int? = null

     var latinName: String? = null

     var alias: String? = null

     var door: String? = null

     var family: String? = null

     var className: String? = null

     var category: String? = null

     var eye: String? = null

     var seed: String? = null

     var photo: String? = null

     var place: String? = null

     var time: String? = null

     var title: String? = null

     var text: String? = null

     var notePhone: String? = null

     var isLeftShowed:Boolean = false

     var isSelected:Boolean = false
     override fun toString(): String {
          return "NoteVo(id=$id, userId=$userId, name=$name, species=$species, latinName=$latinName, alias=$alias, door=$door, family=$family, className=$className, category=$category, eye=$eye, seed=$seed, photo=$photo, place=$place, time=$time, title=$title, text=$text, notePhone=$notePhone, isLeftShowed=$isLeftShowed, isSelected=$isSelected)"
     }


}