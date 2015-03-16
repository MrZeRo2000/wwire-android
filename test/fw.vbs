' **** DIM
Dim wf(200)
wf_pos = 0

' **** Initial data          
lp = 6
lp2 = lp / 2   '3
lt = 12
lt2 = lt / 2   '6
lt4 = lt2 / 2  '3

' **** MAIN PROCEDURE START *****
max_wf_pos = lt * lp + (lp * 2 + 1) * (lt2 - 1) + 1
WScript.Echo "max_wf_pos=" & max_wf_pos
F1WF
WScript.Echo "wf_pos=" & wf_pos
F2WF
WScript.Echo "wf_pos=" & wf_pos
PrintA wf

' **** MAIN PROCEDURE END   *****

Sub F1WF
  for i = 0 to lp * lt - 1
    AppendWF i
  next
End Sub

Sub F2WF
  for i = 1 to lt2 - 1
    AppendWF i - 1

    for j = 0 to lp - 1
      AppendWF i + j * lp * 2
    next

    for j = 0 to lp - 1
      AppendWF lt - i + j * lp * 2
    next            
  next
  AppendWF lt2 - 1
End Sub

Sub AppendWF(v)
  wf(wf_pos) = v
  wf_pos = wf_pos + 1
End Sub

Sub PrintA(va)
  s = "("
  d = ""
  for i1 = lbound(va) to ubound(va)
    s = s & d & va(i1)
    d = ", "
  next
  s = s & ")"
  WScript.echo s
End Sub
