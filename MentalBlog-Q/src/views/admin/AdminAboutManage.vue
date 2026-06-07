<template>
  <div class="anim-fade-in">
    <div class="page-header"><h1 class="page-title">关于我管理</h1><p class="page-desc">管理技能栈、项目经历和成长里程碑</p></div>

    <!-- Skills -->
    <div class="sec-card"><div class="sec-h"><h3>⚡ 技能栈</h3><button class="btn btn-primary btn-sm" @click="showSkillDialog(null)">+ 添加</button></div>
      <el-table :data="skills" size="small" style="width:100%">
        <el-table-column prop="name" label="技能" /><el-table-column prop="level" label="熟练度" width="100" />
        <el-table-column label="进度" min-width="140"><template #default="{row}"><div class="bar"><div :style="{width:row.level+'%'}"></div></div></template></el-table-column>
        <el-table-column label="操作" width="120"><template #default="{row}"><button class="btn btn-ghost btn-sm" @click="showSkillDialog(row)">编辑</button><button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="deleteSkill(row.id)">删除</button></template></el-table-column>
      </el-table>
    </div>

    <!-- Projects -->
    <div class="sec-card"><div class="sec-h"><h3>📁 项目经历</h3><button class="btn btn-primary btn-sm" @click="showProjectDialog(null)">+ 添加</button></div>
      <el-table :data="projects" size="small" style="width:100%">
        <el-table-column prop="name" label="名称" min-width="150" /><el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120"><template #default="{row}"><button class="btn btn-ghost btn-sm" @click="showProjectDialog(row)">编辑</button><button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="deleteProject(row.id)">删除</button></template></el-table-column>
      </el-table>
    </div>

    <!-- Milestones -->
    <div class="sec-card"><div class="sec-h"><h3>🌱 成长里程碑</h3><button class="btn btn-primary btn-sm" @click="showMilestoneDialog(null)">+ 添加</button></div>
      <el-table :data="milestones" size="small" style="width:100%">
        <el-table-column prop="year" label="年份" width="80" /><el-table-column prop="title" label="标题" min-width="150" /><el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120"><template #default="{row}"><button class="btn btn-ghost btn-sm" @click="showMilestoneDialog(row)">编辑</button><button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="deleteMilestone(row.id)">删除</button></template></el-table-column>
      </el-table>
    </div>

    <!-- Skill Dialog -->
    <el-dialog v-model="skillDlg" :title="skillEditId?'编辑技能':'添加技能'" width="400px">
      <div class="fld"><label>名称</label><input v-model="skillForm.name" class="input" /></div>
      <div class="fld"><label>熟练度 (0-100)</label><input v-model.number="skillForm.level" type="number" class="input" min="0" max="100" /></div>
      <template #footer><button class="btn btn-ghost" @click="skillDlg=false">取消</button><button class="btn btn-primary" @click="saveSkill">{{ skillEditId?'保存':'添加' }}</button></template>
    </el-dialog>

    <!-- Project Dialog -->
    <el-dialog v-model="projectDlg" :title="projectEditId?'编辑项目':'添加项目'" width="480px">
      <div class="fld"><label>名称</label><input v-model="projectForm.name" class="input" /></div>
      <div class="fld"><label>描述</label><textarea v-model="projectForm.description" class="input" rows="2"></textarea></div>
      <div class="fld"><label>链接 (可选)</label><input v-model="projectForm.url" class="input" placeholder="https://..." /></div>
      <template #footer><button class="btn btn-ghost" @click="projectDlg=false">取消</button><button class="btn btn-primary" @click="saveProject">{{ projectEditId?'保存':'添加' }}</button></template>
    </el-dialog>

    <!-- Milestone Dialog -->
    <el-dialog v-model="milestoneDlg" :title="milestoneEditId?'编辑里程碑':'添加里程碑'" width="480px">
      <div class="fld"><label>年份</label><input v-model="milestoneForm.year" class="input" placeholder="2026" /></div>
      <div class="fld"><label>标题</label><input v-model="milestoneForm.title" class="input" /></div>
      <div class="fld"><label>描述</label><textarea v-model="milestoneForm.description" class="input" rows="2"></textarea></div>
      <template #footer><button class="btn btn-ghost" @click="milestoneDlg=false">取消</button><button class="btn btn-primary" @click="saveMilestone">{{ milestoneEditId?'保存':'添加' }}</button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminSkills, createSkill, updateSkill, deleteSkill,
         getAdminProjects, createProject, updateProject, deleteProject,
         getAdminMilestones, createMilestone, updateMilestone, deleteMilestone } from '@/api/admin'
import { ElMessage } from 'element-plus'

const skills = ref<any[]>([]); const projects = ref<any[]>([]); const milestones = ref<any[]>([])

onMounted(async () => { try { const [sr, pr, mr] = await Promise.all([getAdminSkills(), getAdminProjects(), getAdminMilestones()]); skills.value = sr.data||[]; projects.value = pr.data||[]; milestones.value = mr.data||[] } catch(e){} })

// Skills
const skillDlg = ref(false); const skillEditId = ref<number|null>(null)
const skillForm = reactive({ name: '', level: 80 })
function showSkillDialog(row: any) { if(row) { skillEditId.value=row.id; Object.assign(skillForm,{name:row.name,level:row.level}) } else { skillEditId.value=null; Object.assign(skillForm,{name:'',level:80}) }; skillDlg.value=true }
async function saveSkill() { try { if(skillEditId.value) await updateSkill(skillEditId.value,skillForm); else await createSkill(skillForm); ElMessage.success('成功'); skillDlg.value=false; const r=await getAdminSkills(); skills.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }
async function deleteSkill(id:number) { try { await deleteSkill(id); ElMessage.success('已删除'); const r=await getAdminSkills(); skills.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }

// Projects
const projectDlg = ref(false); const projectEditId = ref<number|null>(null)
const projectForm = reactive({ name: '', description: '', url: '' })
function showProjectDialog(row: any) { if(row) { projectEditId.value=row.id; Object.assign(projectForm,{name:row.name,description:row.description||'',url:row.url||''}) } else { projectEditId.value=null; Object.assign(projectForm,{name:'',description:'',url:''}) }; projectDlg.value=true }
async function saveProject() { try { if(projectEditId.value) await updateProject(projectEditId.value,projectForm); else await createProject(projectForm); ElMessage.success('成功'); projectDlg.value=false; const r=await getAdminProjects(); projects.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }
async function deleteProject(id:number) { try { await deleteProject(id); ElMessage.success('已删除'); const r=await getAdminProjects(); projects.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }

// Milestones
const milestoneDlg = ref(false); const milestoneEditId = ref<number|null>(null)
const milestoneForm = reactive({ year: '', title: '', description: '' })
function showMilestoneDialog(row: any) { if(row) { milestoneEditId.value=row.id; Object.assign(milestoneForm,{year:row.year,title:row.title,description:row.description||''}) } else { milestoneEditId.value=null; Object.assign(milestoneForm,{year:'',title:'',description:''}) }; milestoneDlg.value=true }
async function saveMilestone() { try { if(milestoneEditId.value) await updateMilestone(milestoneEditId.value,milestoneForm); else await createMilestone(milestoneForm); ElMessage.success('成功'); milestoneDlg.value=false; const r=await getAdminMilestones(); milestones.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }
async function deleteMilestone(id:number) { try { await deleteMilestone(id); ElMessage.success('已删除'); const r=await getAdminMilestones(); milestones.value=r.data||[] } catch(e:any){ElMessage.error(e.message)} }
</script>

<style scoped>
.sec-card { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: var(--radius-xl); padding: 20px; margin-bottom: 20px; }
.sec-h { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.sec-h h3 { font-size: 15px; font-weight: 600; display: flex; align-items: center; gap: 6px; }
.bar { height: 6px; background: var(--color-bg-subtle); border-radius: 3px; overflow: hidden; }
.bar div { height: 100%; background: linear-gradient(90deg, var(--color-primary), var(--accent-400)); border-radius: 3px; }
.fld { margin-bottom: 16px; }
.fld label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--color-text-secondary); }
</style>
